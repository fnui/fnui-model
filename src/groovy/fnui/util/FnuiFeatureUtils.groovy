package fnui.util

import fnui.feature.model.*
import grails.validation.Validateable
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j

/**
 * The FnuiFeatureUtils provides means to get additional information or collect exisiting information
 * for FNUI features.
 */
@CompileStatic
@Log4j
abstract class FnuiFeatureUtils {

    /**
     * Checks if the classFeature describes a domain class.
     *
     * @param classFeature
     * @return
     */
    static boolean isDomainObject(ClassFeature classFeature) {
        classFeature?.artefactType == 'Domain'
    }

    /**
     * Checks if the classFeature describes a command object.
     *
     * @param classFeature
     * @return
     */
    static boolean isCommandObject(ClassFeature classFeature) {
        if (!classFeature.name.endsWith('Command')) {
            return false
        }

        if (!classFeature.getAnnotation(Validateable)) {
            return false
        }

        classFeature.artefactType != 'Domain'
    }

    /**
     * Checks if TypeFeature is validateable. Definition:
     *  - artefactType == 'Domain' or
     *  - has grails.validation.Validateable annotation
     *
     * @param typeFeature
     * @return
     */
    static boolean isValidateable(TypeFeature typeFeature) {
        def feature = typeFeature.feature

        feature ? isValidateable(feature) : false
    }

    /**
     * Checks if ClassFeature is validateable: Defintiion:
     *  - artefactType == 'Domain' or
     *  - has grails.validation.Validateable annotation
     *
     * @param classFeature
     * @return
     */
    static boolean isValidateable(ClassFeature classFeature){
        def hasAnnotation = classFeature.hasAnnotation(Validateable)
        def isDomain = isDomainObject(classFeature)
        return hasAnnotation || isDomain
    }

    /**
     * Collects all used classes of the provided ClassFeature with help of byte code analysis.
     *
     * @param feature
     * @param classRelevanceProvider
     * @return
     */
    static Set<Class> getReferencedClassesWithInternal(ClassFeature feature, ClassRelevanceProvider classRelevanceProvider = null) {
        def classSet = new FnuiClassSet(classRelevanceProvider)

        def bytecodeReferences = FnuiClassUtils.getReferencedClasses(feature.clazz)
        classSet.addAll(bytecodeReferences)

        classSet.remove(feature.clazz)
        return classSet
    }

    /**
     * Collects all classes which are used in the public interfaces of the provided ClassFeature
     *
     * @param feature
     * @param classRelevanceProvider
     * @return
     */
    static Set<Class> getReferencedClasses(ClassFeature feature, ClassRelevanceProvider classRelevanceProvider = null) {
        def classSet = new FnuiClassSet(classRelevanceProvider)

        for (def pf : feature.propertyFeaturesCollection) {
            collectPropertyReferences(pf, classSet)
        }

        for (def pf : feature.staticPropertyFeaturesCollection) {
            collectPropertyReferences(pf, classSet)
        }

        for (def c : feature.constructors) {
            collectMethodReferences(c, classSet)
        }

        for (def mf : feature.methodFeaturesCollection) {
            collectMethodReferences(mf, classSet)
        }

        classSet.remove(feature.clazz)
        return classSet
    }

    private static void collectAnnotationReferences(AbstractFeature feature, FnuiClassSet classSet) {
        feature.annotationCollection.each { af ->
            classSet << af.annotationClass
            af.parameters.values().each { obj ->
                classSet << obj.class
            }
        }
    }

    private static void collectMethodReferences(MethodFeature feature, FnuiClassSet classSet) {
        if (feature.returnType) {
            classSet.addAll(feature.returnType.classesFromTypeDefinition)
        }

        for (def ex : feature.declaredExceptions) {
            classSet.addAll(ex.classesFromTypeDefinition)
        }

        for (def pf : feature.parameterCollection) {
            classSet.addAll(pf.type.classesFromTypeDefinition)
        }

        collectAnnotationReferences(feature, classSet)
    }

    private static void collectPropertyReferences(PropertyFeature feature, FnuiClassSet classSet) {
        classSet.addAll(feature.type.classesFromTypeDefinition)
        collectAnnotationReferences(feature, classSet)
    }
}
