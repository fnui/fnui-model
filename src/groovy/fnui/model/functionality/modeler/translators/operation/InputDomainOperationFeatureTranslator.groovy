package fnui.model.functionality.modeler.translators.operation

import fnui.feature.model.FeatureModel
import fnui.feature.model.MethodFeature
import fnui.model.definition.UserInterfaceDefinition
import fnui.model.functionality.Functionality
import fnui.model.functionality.FunctionalityParameter
import fnui.model.functionality.FunctionalityParameterKind
import fnui.model.functionality.modeler.AbstractFeatureTranslator

/**
 * Translates service methods of which are 'Operation' functionalities.
 */
class InputDomainOperationFeatureTranslator extends AbstractFeatureTranslator {
    final String targetType = 'Operation'

    @Override
    boolean accepts(MethodFeature feature, List<FunctionalityParameter> parameters, UserInterfaceDefinition uiDefintion) {
        acceptOneDomainAndOneCommand(parameters)
    }

    @Override
    Functionality doTranslation(FeatureModel featureModel, Functionality fn, List<FunctionalityParameter> parameters) {
        parameters.each { parameter ->
            if (parameter.parameterKind == FunctionalityParameterKind.DOMAIN) {
                fn.contextNeeded[parameter.name] = parameter.type
            } else {
                fn.inputParameter[parameter.name] = parameter
            }
        }

        return fn
    }
}