package fnui.model.functionality.modeler.translators.update

import fnui.feature.model.FeatureModel
import fnui.feature.model.MethodFeature
import fnui.model.definition.UserInterfaceDefinition
import fnui.model.functionality.Functionality
import fnui.model.functionality.FunctionalityParameter
import fnui.model.functionality.modeler.AbstractFeatureTranslator

/**
 * Translates service methods which matches the domain object update definition.
 *
 * Definition:
 *  - one parameter of type domain
 *  - name begins with update or uiDefinition.type == 'update'
 */
class DomainUpdateFeatureTranslator extends AbstractFeatureTranslator {
    final String targetType = 'Update'

    @Override
    boolean accepts(MethodFeature feature, List<FunctionalityParameter> parameters, UserInterfaceDefinition uiDefintion) {
        acceptOneDomainWithKey('update', feature, parameters, uiDefintion)
    }

    @Override
    Functionality doTranslation(FeatureModel featureModel, Functionality fn, List<FunctionalityParameter> parameterList) {
        def parameter = parameterList.first()

        fn.inputParameter[parameter.name] = parameter
        fn.contextNeeded[parameter.name] = parameter.type

        return fn
    }
}
