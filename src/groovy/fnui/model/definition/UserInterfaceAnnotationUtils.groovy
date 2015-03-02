package fnui.model.definition

/**
 * The UserInterfaceAnnotationUtils provides helper for handling data defined in the
 * UserInterfaceDefinition.
 */
abstract class UserInterfaceAnnotationUtils {

    static RequirementsDescription getRequirementsClosure(UserInterfaceDefinition uiDefinition) {
        def requirementsClosure = uiDefinition.get('requirements')

        if (!requirementsClosure || !(requirementsClosure instanceof Closure)) {
            return null
        }

        new RequirementsDescription(requirementsClosure)
    }
}
