package fnui.model.functionality.contextualize

import fnui.feature.model.TypeFeature

/**
 * Describes the context which is provided by an functionality result.
 */
class FunctionalityContext {
    List<ContextParameter> contextParameters = []

    int size() {
        contextParameters.size()
    }

    void addContextParameter(TypeFeature type, String path, int depth, boolean explicitContext) {
        assert type && depth >= 0

        contextParameters << new ContextParameter(type: type, contextPath: path, depth: depth, explicitContext: explicitContext)
    }

    Map<String,ContextParameter> getProvidableContextParameter(Map<String,TypeFeature> neededContext) {
        def providableContext = [:]

        neededContext.each { name, neededType ->
            def matchingParameter = contextParameters.find { contextParameter ->
                contextParameter.type.isAssignableTo(neededType)
            }

            if (matchingParameter) {
                providableContext[name] = matchingParameter
            }
        }

        return providableContext
    }
}
