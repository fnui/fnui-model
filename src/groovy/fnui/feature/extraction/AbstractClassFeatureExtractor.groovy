package fnui.feature.extraction

import fnui.feature.model.*
import fnui.util.ClassRelevanceProvider
import fnui.util.constraints.ConstraintsDefinition
import fnui.util.constraints.ConstraintsParser
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.springframework.core.LocalVariableTableParameterNameDiscoverer
import org.springframework.core.ParameterNameDiscoverer

import java.beans.Introspector
import java.lang.annotation.Annotation
import java.lang.reflect.*

/**
 * Provides common methods for specialized ClassFeatureExtractions
 */
@CompileStatic
@Log4j
abstract class AbstractClassFeatureExtractor implements ClassFeatureExtractor {
    static final List<String> ANNOTATION_INTERFACE_METHODS = Annotation.declaredMethods.collect { Method m -> m.name }
    static final List<String> GROOVY_OBJECT_METHODS = GroovyObject.methods.collect { Method m -> m.name }
    static final List<String> OBJECT_METHODS = Object.methods.collect { Method m -> m.name }
    static final List<String> BASIC_METHODS = ['clone', 'finalize', 'swapInit']
    static final List<String> BASIC_PROPERTIES = ['class', 'metaClass']
    static ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer()

    GrailsApplication grailsApplication
    FeatureModel featureModel
    ClassRelevanceProvider classRelevanceProvider

    protected ClassFeature newClassFeature(Class clazz, String artefactType) {
        def feature = new ClassFeature(featureModel, clazz, artefactType)

        feature.featureExtractor = this.getClass().name

        return feature
    }

    protected void extractBasicInfos(Class clazz, ClassFeature feature) {
        feature.name = clazz.simpleName
        feature.packageName = clazz.package?.name
        feature.superClass = clazz.superclass
        feature.modifier = clazz.modifiers

        extractInterfaces(clazz, feature)

        extractConstructors(clazz, feature)

        extractAnnotationFeatures(feature, clazz.annotations)
    }

    protected void extractInterfaces(Class clazz, ClassFeature classFeature) {
        for (Class i : GrailsClassUtils.getAllInterfacesForClassAsSet(clazz)) {
            classFeature.interfaces << i
        }
    }

    protected void extractMethodFeatures(Class clazz, ClassFeature classFeature) {
        for (Method method : clazz.methods) {
            if (method.isSynthetic()) {
                continue
            }

            if (!Modifier.isPublic(method.modifiers)) {
                continue
            }

            def methodName = method.name
            if (methodName.indexOf('$') != -1) {
                continue
            }

            boolean isGetter = methodName.length() > 3 && methodName.startsWith('get')
            boolean isSetter = methodName.length() > 3 && methodName.startsWith('set')
            boolean isBooleanGetter = methodName.length() > 2 && methodName.startsWith('is') && (method.returnType == Boolean.class || method.returnType == boolean.class)

            if (isGetter || isSetter || isBooleanGetter) {
                continue
            }

            def isMethodDefinedByObjectBaseClass = (GroovyObject.isAssignableFrom(clazz) && GROOVY_OBJECT_METHODS.contains(methodName)) || OBJECT_METHODS.contains(methodName) || BASIC_METHODS.contains(methodName) || methodName =~ /\d/
            if (isMethodDefinedByObjectBaseClass) {
                continue
            }

            def methodFeature = new MethodFeature()
            methodFeature.name = method.name
            extractAnnotationFeatures(methodFeature, method.annotations)
            methodFeature.returnType = new TypeFeature(featureModel, method.genericReturnType)

            def parameterNames = parameterNameDiscoverer.getParameterNames(method)
            def parameters = method.parameters
            for (int i = 0; i < parameters.length; i++) {
                def name = parameterNames ? parameterNames[i] : "arg${i}".toString()
                def parameter = parameters[i]
                def parameterFeature = new PropertyFeature()

                parameterFeature.name = name
                parameterFeature.type = new TypeFeature(featureModel, parameter.parameterizedType)

                extractAnnotationFeatures(parameterFeature, parameter.annotations)

                parameterFeature.containingClass = classFeature

                methodFeature.parameterOrder << name
                methodFeature.parameters[name] = parameterFeature
            }

            for (Type exceptionType : method.genericExceptionTypes) {
                methodFeature.declaredExceptions << new TypeFeature(featureModel, exceptionType)
            }

            if (GrailsClassUtils.isPublicStatic(method)) {
                classFeature.addStaticMethod(methodFeature)
            } else {
                classFeature.addMethod(methodFeature)
            }
        }
    }

    protected void extractConstructors(Class clazz, ClassFeature classFeature) {
        for (Constructor<?> constructor : clazz.constructors) {
            def constructorFeature = new MethodFeature()
            constructorFeature.name = constructor.name
            extractAnnotationFeatures(constructorFeature, constructor.annotations)

            def parameterNames = parameterNameDiscoverer.getParameterNames(constructor)
            def parameters = constructor.parameters
            for (int i = 0; i < parameters.length; i++) {
                def name = parameterNames ? parameterNames[i] : "arg${i}".toString()
                def parameter = parameters[i]
                def parameterFeature = new PropertyFeature()

                parameterFeature.name = name
                parameterFeature.type = new TypeFeature(featureModel, parameter.parameterizedType)

                extractAnnotationFeatures(parameterFeature, parameter.annotations)

                constructorFeature.parameters[name] = parameterFeature
            }

            for (Type exceptionType : constructor.genericExceptionTypes) {
                constructorFeature.declaredExceptions <<  new TypeFeature(featureModel, exceptionType)
            }

            classFeature.addConstructor(constructorFeature)
        }
    }

    protected void extractAnnotationFeatures(AbstractFeature feature, Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            def type = annotation.annotationType()

            def af = new AnnotationFeature()
            af.annotationClass = type

            for (Method m : type.methods.findAll { !ANNOTATION_INTERFACE_METHODS.contains(it.name) }) {
                af.setParameter(m.name, m.invoke(annotation))
            }

            feature.addAnnotationFeature(af)
        }
    }

    protected void extractPropertyFeatures(Class clazz, ClassFeature classFeature, Collection<String> ignoredPropertyNames) {
        ConstraintsDefinition constraints = ConstraintsParser.getConstraints(clazz)
        List<Class> classHierarchy = resolveClassHierarchy(clazz)

        extractPropertyFeaturesFromFields(clazz, classFeature, classHierarchy, constraints, ignoredPropertyNames)
        extractPropertyFeaturesFromMethods(clazz, classFeature, classHierarchy, constraints, ignoredPropertyNames)
        //extractPropertyFeaturesFromBean(clazz, containingClass, constraints, ignoredPropertyNames)
    }

    private void extractPropertyFeaturesFromFields(Class clazz, ClassFeature classFeature, List<Class> classHierarchy, ConstraintsDefinition constraints, Collection<String> ignoredPropertyNames) {
        for (def c : classHierarchy) {
            Field[] fields = c.declaredFields
            for (def f : fields) {
                try {
                    if (f.isSynthetic()) {
                        continue
                    }

                    int modifiers = f.getModifiers();
                    if (!Modifier.isPublic(modifiers)) {
                        continue
                    }

                    def name = f.name
                    if (name.indexOf('$') != -1) {
                        continue
                    }

                    if (ignoredPropertyNames.contains(name)) {
                        continue
                    }

                    boolean isStatic = Modifier.isStatic(modifiers)

                    if (isStatic) {
                        def propertyFeature = new StaticPropertyFeature()
                        propertyFeature.name = name
                        propertyFeature.type = new TypeFeature(featureModel, f.genericType)
                        extractAnnotationFeatures(propertyFeature, f.annotations)
                        propertyFeature.defaultValue = GrailsClassUtils.getStaticFieldValue(clazz, name)
                        classFeature.addStaticProperty(propertyFeature)
                    } else {
                        def propertyFeature = new PropertyFeature()
                        propertyFeature.name = name
                        propertyFeature.type = new TypeFeature(featureModel, f.genericType)
                        extractAnnotationFeatures(propertyFeature, f.annotations)
                        def propertyConstraints = constraints[name]

                        if (propertyConstraints) {
                            propertyFeature.constraints.putAll(propertyConstraints)
                        }
                    }
                } catch (IllegalAccessException ex) {
                    throw new IllegalStateException("Shouldn't be illegal to access field '${f.name}': ${ex}");
                }
            }
        }
    }

    private void extractPropertyFeaturesFromMethods(Class clazz, ClassFeature classFeature, List<Class> classHierarchy, ConstraintsDefinition constraints, Collection<String> ignoredPropertyNames) {
        for (def c : classHierarchy) {
            Method[] methods = c.declaredMethods
            for (def m : methods) {
                try {
                    if (m.isSynthetic()) {
                        continue
                    }

                    int modifiers = m.modifiers
                    boolean isPublic = Modifier.isPublic(modifiers)
                    if (!isPublic) {
                        continue
                    }

                    String name = m.name
                    boolean isVoidOrNonParameterlessOrLocalMethod = m.returnType == Void.class || m.parameterTypes.length != 0 || name.indexOf('$') != -1
                    if (isVoidOrNonParameterlessOrLocalMethod) {
                        continue
                    }

                    if (name.length() > 3 && name.startsWith("get")) {
                        name = Introspector.decapitalize(name.substring(3))
                    } else if (name.length() > 2 && name.startsWith("is") && (m.returnType == Boolean.class || m.returnType == boolean.class)) {
                        name = Introspector.decapitalize(name.substring(2))
                    } else {
                        continue
                    }

                    if (ignoredPropertyNames.contains(name)) {
                        continue
                    }

                    boolean isStatic = Modifier.isStatic(modifiers)

                    if (isStatic) {
                        def propertyFeature = classFeature.staticPropertyFeatures[name] ?: new StaticPropertyFeature()
                        propertyFeature.name = name
                        propertyFeature.type = propertyFeature.type ?: new TypeFeature(featureModel, m.genericReturnType)
                        extractAnnotationFeatures(propertyFeature, m.annotations)

                        def propertyConstraints = constraints[name]
                        if (propertyConstraints) {
                            propertyFeature.constraints.putAll(propertyConstraints)
                        }
                        // For static getter methods no value is retrieved, because the logic of the getter could be non-trivial and a exception could occur.
                        classFeature.addStaticProperty(propertyFeature)
                    } else {
                        def propertyFeature = classFeature.propertyFeatures[name] ?: new PropertyFeature()
                        propertyFeature.name = name
                        propertyFeature.type = propertyFeature.type ?: new TypeFeature(featureModel, m.genericReturnType)
                        extractAnnotationFeatures(propertyFeature, m.annotations)

                        Field matchingField = null

                        try {
                            matchingField = c.getDeclaredField(name)
                        } catch (NoSuchFieldException) {
                        }

                        if (matchingField) {
                            extractAnnotationFeatures(propertyFeature, matchingField.annotations)
                        }

                        def propertyConstraints = constraints[name]
                        if (propertyConstraints) {
                            propertyFeature.constraints.putAll(propertyConstraints)
                        }

                        classFeature.addProperty(propertyFeature)
                    }
                } catch (IllegalAccessException ex) {
                    throw new IllegalStateException("Shouldn't be illegal to access m '${m.name}': ${ex}");
                }
            }
        }
    }

    private List<Class<?>> resolveClassHierarchy(Class<?> c) {
        List<Class<?>> list = new ArrayList<Class<?>>();
        Class<?> currentClass = c;
        while (currentClass != null) {
            list.add(currentClass);
            currentClass = currentClass.getSuperclass();
        }
        Collections.reverse(list);
        return list;
    }
}