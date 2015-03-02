package fnui.model.functionality.modeler

import fnui.feature.model.FeatureModel
import fnui.feature.model.MethodFeature
import fnui.model.definition.UserInterfaceDefinition
import fnui.model.functionality.Functionality
import fnui.model.functionality.FunctionalityParameter
import fnui.model.functionality.FunctionalityParameterKind

/**
 * Provides common util function eg for categorization of method parameters.
 */
abstract class AbstractFeatureTranslator implements FeatureTranslator {
    final static String UI_DEFINITION_TYPE_KEY = 'type'

    @Override
    Functionality translate(MethodFeature feature, List<FunctionalityParameter> parameters, UserInterfaceDefinition uiDefintion) {
        def fn = new Functionality()
        fn.methodFeature = feature
        fn.functionalityType = this.targetType
        fn.uiDefinition = uiDefintion
        fn.viewModel = fn.methodFeature.returnType

        def fm = fn.methodFeature.classFeature.featureModel
        return doTranslation(fm, fn, parameters)
    }

    /**
     * Fills the provided Functionality with functionalityType specific information.
     *
     * The pre-initialization of the Functionality consists of:
     *  - Creating new Functionality
     *  - Setting:
     *   - methodFeature
     *   - functionalityType = translator.targetType
     *   - uiDefinition
     *   - viewModel
     *
     * @param featureModel
     * @param fn pre-initialize functionality
     * @param parameterList contains the pre-categorized service method parameters
     * @return
     */
    abstract Functionality doTranslation(FeatureModel featureModel, Functionality fn, List<FunctionalityParameter> parameters)

    protected static boolean acceptOneDomainWithKey(String typeKey, MethodFeature feature, List<FunctionalityParameter> parameters, UserInterfaceDefinition uiDefinition) {
        boolean nameMatches = acceptNameOrUiDefinitionType(typeKey, feature, uiDefinition)
        return nameMatches && acceptOneDomain(parameters)
    }
    protected static boolean acceptOneCommandWithKey(String typeKey, MethodFeature feature, List<FunctionalityParameter> parameters, UserInterfaceDefinition uiDefinition) {
        boolean nameMatches = acceptNameOrUiDefinitionType(typeKey, feature, uiDefinition)
        return nameMatches && acceptOneCommand(parameters)
    }

    protected static boolean acceptOneDomain(List<FunctionalityParameter> parameters) {
        if (parameters.size() != 1) {
            return false
        }

        if (parameters.first().parameterKind != FunctionalityParameterKind.DOMAIN) {
            return false
        }

        return true
    }

    protected static boolean acceptOneCommand(List<FunctionalityParameter> parameters) {
        if (parameters.size() != 1) {
            return false
        }

        if (parameters.first().parameterKind != FunctionalityParameterKind.COMMAND) {
            return false
        }

        return true
    }

    protected static boolean acceptOneDomainAndOneCommand(List<FunctionalityParameter> parameters) {
        if (parameters.size() != 2) {
            return false
        }

        [FunctionalityParameterKind.COMMAND, FunctionalityParameterKind.DOMAIN].every { kind ->
            parameters.any { parameter ->
                kind == parameter.parameterKind
            }
        }
    }

    protected static boolean acceptNoParameters(List<FunctionalityParameter> parameters) {
        return parameters.size() == 0
    }

    protected static boolean acceptNameOrUiDefinitionType(String typeKey, MethodFeature feature, UserInterfaceDefinition uiDefinition) {
        def uiDefinitionValue = uiDefinition.getMethodValue(UI_DEFINITION_TYPE_KEY)
        if (uiDefinitionValue != null) {
            return uiDefinitionValue == typeKey
        } else {
            feature.name.startsWith(typeKey)
        }
    }
}
