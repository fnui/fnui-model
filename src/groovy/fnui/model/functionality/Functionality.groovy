package fnui.model.functionality

import fnui.feature.model.MethodFeature
import fnui.feature.model.TypeFeature
import fnui.model.definition.RequirementsDescription
import fnui.model.definition.UserInterfaceAnnotationUtils
import fnui.model.definition.UserInterfaceDefinition
import fnui.util.FnuiConventions
import groovy.transform.Canonical
import groovy.transform.ToString

/**
 * A Functionality describes a certain method provided by a service which provides a
 * functionality which can be used via the UI.
 */
@Canonical(excludes = 'group')
@ToString(excludes = 'group', includeNames = true)
class Functionality {
    /**
     * Defines the kind of the functionality. The possible values depends on the available
     * FeatureTranslator and following model generation phases.
     */
    String functionalityType

    MethodFeature methodFeature
    FunctionalityGroup group

    /**
     * Definitions for the functionality defined in the closure of the UserInterface
     * annotation.
     */
    UserInterfaceDefinition uiDefinition

    /**
     * Parameter provided by user input
     */
    Map<String,FunctionalityParameter> inputParameter = [:]

    /**
     * Parameter provided by context of the functionality
     */
    Map<String,TypeFeature> contextNeeded = [:]

    /**
     * ViewModel available for the result presentation of the functionality.
     */
    TypeFeature viewModel

    List<FunctionalityLink> getOutGoingLinks() {
        group.model.fromFunctionalityLinks[this]
    }

    /**
     * @return the conventional varName for the service which is used for dependency injection
     */
    String getServiceVarName() {
        group.serviceVarName
    }

    /**
     * name for the functionality (same as underlying method name)
     * @return
     */
    String getName() {
        methodFeature.name
    }

    /**
     * @return name of the service method
     */
    String getServiceMethodName() {
        name
    }

    /**
     * @return conventional variable name for the viewModel result
     */
    String getViewModelVarName() {
        FnuiConventions.getVarName(viewModel, 'Instance')
    }

    FunctionalityModel getFunctionalityModel() {
        group.model
    }

    RequirementsDescription getRequirements() {
        UserInterfaceAnnotationUtils.getRequirementsClosure(uiDefinition)
    }
}
