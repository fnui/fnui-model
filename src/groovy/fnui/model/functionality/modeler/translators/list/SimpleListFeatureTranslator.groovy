package fnui.model.functionality.modeler.translators.list

import fnui.feature.model.FeatureModel
import fnui.feature.model.MethodFeature
import fnui.model.definition.UserInterfaceDefinition
import fnui.model.functionality.Functionality
import fnui.model.functionality.FunctionalityParameter
import fnui.model.functionality.modeler.AbstractFeatureTranslator
import groovy.util.logging.Log4j

/**
 * Translates service methods which matches the simplest list definition.
 *
 * Definition:
 *  - no parameters
 *  - returnType is collection
 */
@Log4j
class SimpleListFeatureTranslator extends AbstractFeatureTranslator {
    final String targetType = 'List'

    @Override
    boolean accepts(MethodFeature feature, List<FunctionalityParameter> parameters, UserInterfaceDefinition uiDefintion) {
        if (!acceptNoParameters(parameters)) return false

        return feature.returnType.isCollection()
    }

    @Override
    Functionality doTranslation(FeatureModel featureModel, Functionality fn, List<FunctionalityParameter> parameters) {
        return fn
    }
}
