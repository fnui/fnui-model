package fnui.model.functionality

import fnui.feature.model.PropertyFeature
import fnui.feature.model.TypeFeature

/**
 * FunctionalityParameter description used in translation process.
 */
class FunctionalityParameter {
    String name
    PropertyFeature propertyFeature
    TypeFeature type
    FunctionalityParameterKind parameterKind
    Map<String, TypeFeature> containedContext = [:]

    /**
     * @return the containedContext mapping after transformation of the key to describe the access path
     */
    Map<String, TypeFeature> getParameterContext() {
        def parameterContext = [:]

        containedContext.each { propertyName, type ->
            String pathKey = "${name}.${propertyName}"
            parameterContext[pathKey] = type
        }

        return parameterContext
    }
}
