package fnui.model.task

import fnui.feature.model.TypeFeature
import fnui.model.definition.RequirementsDescription
import fnui.model.functionality.FunctionalityLink
import fnui.model.functionality.FunctionalityLinkKind
import fnui.model.functionality.contextualize.ContextParameter

/**
 * TaskFlowLinks connects TaskFlows like FunctionalityLinks the matching functionalities.
 */
class TaskFlowLink {
    TaskFlow from
    TaskFlow to

    FunctionalityLink functionalityLink

    FunctionalityLinkKind getLinkKind() {
        functionalityLink.linkKind
    }

    Map<String, TypeFeature> getNeededContext() {
        functionalityLink.neededContext
    }

    /**
     * @return context available from viewModel of the from flow
     */
    Map<String,ContextParameter> getFromProvidedContext() {
        functionalityLink.fromProvidedContext
    }

    /**
     * Checks if this link has the specified neededContext
     *
     * @param context
     * @return true if matching
     */
    boolean matchesContext(List<TypeFeature> context) {
        functionalityLink.matchesContext(context)
    }

    /**
     * @return the requirements defined for the link target
     */
    RequirementsDescription getRequirements() {
        return functionalityLink.requirements
    }
}
