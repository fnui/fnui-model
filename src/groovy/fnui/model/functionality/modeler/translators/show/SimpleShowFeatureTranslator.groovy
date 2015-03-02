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
 *  - no parameters
 *  - name begins with show or uiDefinition.type == 'show'
 */
@Log4j
class SimpleShowFeatureTranslator extends AbstractFeatureTranslator {
    final String targetType = 'Show'

    @Override
    boolean accepts(MethodFeature feature, List<FunctionalityParameter> parameters, UserInterfaceDefinition uiDefintion) {
        acceptNoParameters(parameters) && acceptNameOrUiDefinitionType('show', feature, uiDefintion)
    }

    @Override
    Functionality doTranslation(FeatureModel featureModel, Functionality fn, List<FunctionalityParameter> parameterList) {
        return fn
    }
}
