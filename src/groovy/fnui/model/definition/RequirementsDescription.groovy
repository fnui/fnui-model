package fnui.model.definition

import fnui.model.functionality.contextualize.ContextParameter

class RequirementsDescription {
    Class closureClass
    List<Class> parameters = []

    Map<Class,String> parameterMapping = [:]

    RequirementsDescription(Closure closure) {
        closureClass = closure.getClass()
        parameters.addAll(closure.parameterTypes)
    }

    void defineParameterMapping(Map<String, ContextParameter> fromProvidedContext) {
        parameters.every { Class clazz ->
            def contextParameter = fromProvidedContext.find { _, p -> p.type.isAssignableTo(clazz) }.value

            parameterMapping[clazz] = contextParameter.contextPath

            return contextParameter != null
        }
    }

    String generateParams(String varName) {
        def paramsList = parameters.collect{ clazz ->
            String path = parameterMapping[clazz]
            path ? "${varName}?.${path}" : varName
        }

        return paramsList.toString()
    }

    String toString() {
        "(parameter:${parameters}, parameterMapping:${parameterMapping})"
    }
}
