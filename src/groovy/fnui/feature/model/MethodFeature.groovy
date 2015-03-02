package fnui.feature.model

import groovy.transform.CompileStatic
import groovy.transform.ToString

/**
 * A MethodFeature describes a method of an class.
 */
@CompileStatic
@ToString(includes = ['name', 'returnType', 'parameters'], includeNames = true)
class MethodFeature extends AbstractFeature {
    /**
     * the containing ClassFeature
     */
    ClassFeature classFeature

    /**
     * the type description of the return value
     */
    TypeFeature returnType

    /**
     * the explicitly defined exceptions of the method
     */
    List<TypeFeature> declaredExceptions = []

    /**
     * the order of the parameters
     */
    List<String> parameterOrder = []

    /**
     * the parameter mapping
     */
	Map<String, PropertyFeature> parameters = [:]

    /**
     * @return a view to the parameter mapping
     */
    Collection<PropertyFeature> getParameterCollection() {
        parameters.values()
    }
}
