package fnui.model.functionality.contextualize

import fnui.feature.model.TypeFeature

/**
 * A ContextParameter describes a providable context object of a view model.
 */
class ContextParameter {
    /**
     * Type of the providable context
     */
    TypeFeature type

    /**
     * access path in viewModel
     */
    String contextPath

    /**
     * access depth
     */
    Integer depth

    /**
     * Is explicitly marked as context?
     */
    Boolean explicitContext
}
