package fnui.feature.model

import fnui.core.viewmodel.ListViewModel
import fnui.util.FnuiClassSet
import fnui.util.FnuiClassUtils
import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.commons.GrailsStringUtils
import org.springframework.core.ResolvableType

import java.lang.reflect.Type

/**
 * The TypeFeature describes a type definition of an feature (eg property or method return-value)
 */
@CompileStatic
@EqualsAndHashCode(includes = ['type'], includeFields = true)
@Log4j
class TypeFeature {
    private final FeatureModel featureModel
    private final ResolvableType type

    TypeFeature(FeatureModel featureModel, Type type) {
        assert featureModel && type
        this.featureModel = featureModel
        this.type = ResolvableType.forType(type)
    }

    TypeFeature(FeatureModel featureModel, ResolvableType resolvableType) {
        assert featureModel && resolvableType
        this.featureModel = featureModel
        this.type = resolvableType
    }

    TypeFeature(ClassFeature classFeature) {
        assert classFeature
        this.featureModel = classFeature.featureModel
        this.type = ResolvableType.forType(classFeature.clazz)
    }

    /**
     * @return the related class feature
     */
    ClassFeature getFeature() {
        featureModel.getFeaturesOfClass(rawClass)
    }

    /**
     * @return the raw class of the type definition
     */
    Class getRawClass() {
        type.rawClass
    }

    /**
     * Checks the raw type compatibility between this and the other TypeFeature
     *
     * @param other
     * @return true if other is assignable to this
     */
    boolean isAssignableFrom(TypeFeature other) {
        isAssignableFrom(other.rawClass)
    }

    /**
     * Checks the raw type compatibility between the represented class of this feature and the other class
     *
     * @param other
     * @return true if other is assignable to this
     */
    boolean isAssignableFrom(Class other) {
        rawClass.isAssignableFrom(other)
    }

    /**
     * Checks the raw type compatibility between the represented class of this feature and the other class
     *
     * @param other
     * @return true if this is assignable to the other
     */
    boolean isAssignableTo(TypeFeature other) {
        isAssignableTo(other.rawClass)
    }

    /**
     * Checks the raw type compatibility between the represented class of this feature and the other class
     *
     * @param other
     * @return true if this is assignable to the other
     */
    boolean isAssignableTo(Class other) {
        other.isAssignableFrom(rawClass)
    }

    /**
     * Checks if the provided class is the rawClass.
     *
     * @param other class to compare with
     * @return true if the rawClass match the other class
     */
    boolean isRawClass(Class other) {
        rawClass == other
    }

    /**
     * @return true if this describes a Collection
     */
    boolean isCollection() {
        Collection.isAssignableFrom(rawClass)
    }

    /**
     * @return true if this describes a ListViewModel
     */
    boolean isListViewModel() {
        ListViewModel.isAssignableFrom(rawClass)
    }

    /**
     * @return itself if it is not an generic otherwise a TypeFeature for the type parameter
     */
    TypeFeature getDescribedType() {
        isCollection() || isListViewModel() ? new TypeFeature(featureModel, type.getGeneric(0)) : this
    }

    /**
     * See {@link FnuiClassUtils}
     *
     * @return set of referenced classes
     */
    Set<Class> getClassesFromTypeDefinition() {
        getClassesOfType(type)
    }

    /**
     * Extract all referenced classes for this type feature. For non-generic types
     * this collection consists of the base class, for generic types also of all
     * parameter types and for arrays the base type.
     *
     * @param type
     * @return set of class references
     */
    private static Set<Class> getClassesOfType(ResolvableType type) {
        //log.info type
        def classes = new FnuiClassSet()

        if (type.isArray()) {
            classes.addAll(getClassesOfType(type.componentType))
        } else {
            classes << type.rawClass

            if (!type.hasUnresolvableGenerics()) {
                for (def generic : type.generics) {
                    classes.addAll(getClassesOfType(generic))
                }
            }
        }

        return classes
    }

    @Override
    String toString() {
        type.toString()
    }

    /**
     * @return the full qualified name with generic definitions for the described type
     */
    String getClassDeclaration() {
        type.toString()
    }

    /**
     * @return the full qualified name for the raw class (eg for import-statements)
     */
    String getRawClassDeclaration() {
        def cDef = getClassDeclaration()
        if (type.hasGenerics()) {
            cDef = GrailsStringUtils.substringBefore(cDef, '<')
        }
        return cDef
    }

    /**
     * @return the short class declaration with generic definitions for the described type
     */
    String getShortClassDeclaration() {
        GrailsStringUtils.substringAfterLast(getClassDeclaration(), '.')
    }
}
