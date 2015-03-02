package fnui.model.functionality

/**
 * A link between functionalities can exist because:
 *  - the functionalities are in the same group and the context is provided
 *  - the from-fn can provide the context needed by the to-fn
 */
enum FunctionalityLinkKind {
    /**
     * A group link is a group relevant link which does not need an specific context.
     */
    GROUP_LINK,

    /**
     * A link which is added based on the explicit context of the from-functionality.
     *
     * Operations are normally added based on the explicit context only.
     */
    EXPLICIT_CONTEXT_LINK,

    /**
     * A link which is added based on the implicit context of the from-functionality.
     *
     * List- and Show-tasks can be added based on the implicit context. This allows an
     * information-based traversal of the application domain.
     */
    IMPLICIT_CONTEXT_LINK
}
