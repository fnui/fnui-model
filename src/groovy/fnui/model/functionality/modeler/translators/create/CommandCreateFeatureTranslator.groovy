package fnui.model.functionality.modeler.translators.create

import fnui.feature.model.FeatureModel
import fnui.feature.model.MethodFeature
import fnui.model.definition.UserInterfaceDefinition
import fnui.model.functionality.Functionality
import fnui.model.functionality.FunctionalityParameter
import fnui.model.functionality.modeler.AbstractFeatureTranslator

/**
 * Translates service methods which matches the domain object create definition.
 *
 * Definition:
 *  - one parameter of type domain
 *  - name begins with create or uiDefinition.type == 'create'
 */
class CommandCreateFeatureTranslator extends AbstractFeatureTranslator {
    final String targetType = 'Create'

    @Override
    boolean accepts(MethodFeature feature, List<FunctionalityParameter> parameters, UserInterfaceDefinition uiDefintion) {
        acceptOneCommandWithKey('create', feature, parameters, uiDefintion)
    }

    @Override
    Functionality doTranslation(FeatureModel featureModel, Functionality fn, List<FunctionalityParameter> parameters) {
        def parameter = parameters.first()

        fn.inputParameter[parameter.name] = parameter
        fn.contextNeeded.putAll(parameter.parameterContext)

        return fn
    }
}
