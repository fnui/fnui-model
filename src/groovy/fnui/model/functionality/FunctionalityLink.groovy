package fnui.model.functionality

import fnui.feature.model.TypeFeature
import fnui.model.definition.RequirementsDescription
import fnui.model.functionality.contextualize.ContextParameter

/**
 * Link between to functionalities with the meaning of an navigational option.
 *
 * If an user has used functionality from navigation to functionality to could
 * be provided by the generated interface.
 */
class FunctionalityLink {
    /**
     * The FunctionalityLinkKind separates links in different categories.
     */
    FunctionalityLinkKind linkKind

    Functionality from
    Functionality to

    /**
     * Context needed for calling of Functionality to.
     */
    Map<String, TypeFeature> neededContext = [:]

    /**
     * Context provided by Functionality from for calling of to.
     */
    Map<String, ContextParameter> fromProvidedContext = [:]

    /**
     * Additional needed context which is not directly defined by from.
     *
     * @return list of parameters additionally needed by Functionality to
     */
    List<String> getOpenContext() {
        neededContext.keySet().collect { context -> !fromProvidedContext.containsKey(context)}
    }

    /**
     * Checks if this link has the specified neededContext
     *
     * @param context
     * @return true if matching
     */
    boolean matchesContext(List<TypeFeature> context) {
        context.every { type ->
            neededContext.any { name, otherType ->
                otherType == type
            }
        }
    }

    /**
     * @return the requirements defined for the link target
     */
    RequirementsDescription getRequirements() {
        def requirements = to.requirements

        requirements?.defineParameterMapping(fromProvidedContext)

        return requirements
    }

    String toString() {
        "Link (${linkKind}) ${from.name} => ${to.name}"
    }
}
