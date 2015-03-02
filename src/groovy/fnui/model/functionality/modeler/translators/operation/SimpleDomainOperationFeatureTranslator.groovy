package fnui.model.functionality.modeler.translators.operation

import fnui.feature.model.FeatureModel
import fnui.feature.model.MethodFeature
import fnui.model.definition.UserInterfaceDefinition
import fnui.model.functionality.Functionality
import fnui.model.functionality.FunctionalityParameter
import fnui.model.functionality.modeler.AbstractFeatureTranslator

/**
 * Translates service methods of which are 'Operation' functionalities.
 */
class SimpleDomainOperationFeatureTranslator extends AbstractFeatureTranslator {
    final String targetType = 'Operation'

    @Override
    boolean accepts(MethodFeature feature, List<FunctionalityParameter> parameters, UserInterfaceDefinition uiDefintion) {
        acceptOneDomain(parameters)
    }

    @Override
    Functionality doTranslation(FeatureModel featureModel, Functionality fn, List<FunctionalityParameter> parameters) {
        def parameter = parameters.first()

        fn.contextNeeded[parameter.name] = parameter.type

        return fn
    }
}