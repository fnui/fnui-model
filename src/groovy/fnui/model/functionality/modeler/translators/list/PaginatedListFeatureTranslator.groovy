package fnui.model.functionality.modeler.translators.list

import fnui.core.command.ListCommand
import fnui.core.viewmodel.ListViewModel
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
class PaginatedListFeatureTranslator extends AbstractFeatureTranslator {
    final String targetType = 'List'

    @Override
    boolean accepts(MethodFeature feature, List<FunctionalityParameter> parameters, UserInterfaceDefinition uiDefintion) {
        if (!acceptListCommand(parameters)) return false

        return returnsListViewModel(feature)
    }

    @Override
    Functionality doTranslation(FeatureModel featureModel, Functionality fn, List<FunctionalityParameter> parameters) {
        def parameter = parameters.first()

        fn.inputParameter[parameter.name] = parameter

        return fn
    }

    protected static boolean acceptListCommand(List<FunctionalityParameter> parameters) {
        if (parameters.size() != 1)
            return false

        if (!parameters.first().type.isAssignableTo(ListCommand))
            return false

        return true
    }

    protected static boolean returnsListViewModel(MethodFeature feature) {
        feature.returnType.isAssignableTo(ListViewModel)
    }
}
