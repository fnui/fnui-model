package fnui.model.functionality.modeler

import fnui.feature.model.MethodFeature
import fnui.model.definition.UserInterfaceDefinition
import fnui.model.functionality.Functionality
import fnui.model.functionality.FunctionalityParameter

/**
 * Translates a service method into a functionality of the functionalityModel, if it accepts the related methodFeature
 * for translation
 */
interface FeatureTranslator {
    /**
     * The translator target type defines the functionalityType on returned functionalities.
     *
     * @return name for the translator
     */
    String getTargetType()

    /**
     * Checks if the methodfeature could be translated into an functionality by this translator
     *
     * @param feature method for which to generator a Functionality
     * @param parameters pre-categorized method parameters
     * @param uiDefintion for the method
     * @return true if translatable
     */
    boolean accepts(MethodFeature feature, List<FunctionalityParameter> parameters, UserInterfaceDefinition uiDefintion)

    /**
     * Translate a feature if it is acceptable for the translator.
     *
     * @param feature method for which to generator a Functionality
     * @param parameters pre-categorized method parameters
     * @param uiDefintion for the method
     * @return the new functionality if translation was possible otherwise null
     */
    Functionality translate(MethodFeature feature, List<FunctionalityParameter> parameters, UserInterfaceDefinition uiDefintion)
}
