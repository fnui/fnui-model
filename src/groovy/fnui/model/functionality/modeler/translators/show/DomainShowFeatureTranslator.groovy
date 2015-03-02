package fnui.model.functionality.modeler.translators.show

import fnui.feature.model.FeatureModel
import fnui.feature.model.MethodFeature
import fnui.model.definition.UserInterfaceDefinition
import fnui.model.functionality.Functionality
import fnui.model.functionality.FunctionalityParameter
import fnui.model.functionality.modeler.AbstractFeatureTranslator
import groovy.util.logging.Log4j

/**
 * Translates service methods which matches this definition:
 *  - one parameter of type domain
 *  - name begins with show or uiDefinition.type == 'show'
 */
@Log4j
class DomainShowFeatureTranslator extends AbstractFeatureTranslator {
    final String targetType = 'Show'

    @Override
    boolean accepts(MethodFeature feature, List<FunctionalityParameter> parameters, UserInterfaceDefinition uiDefintion) {
        acceptOneDomainWithKey('show', feature, parameters, uiDefintion)
    }

    @Override
    Functionality doTranslation(FeatureModel featureModel, Functionality fn, List<FunctionalityParameter> parameterList) {
        def parameter = parameterList.first()

        fn.contextNeeded[parameter.name] = parameter.type

        return fn
    }
}
