package fnui.feature

/**
 * Defines the detail level of the feature model.
 */
enum FeatureModelDetailLevel {
    /**
     * All application-classes which may be relevant according to ClassRelevanceProvider are part of the model.
     */
    FULL_MODEL,
    /**
     * Only classes related to classes which are used inside the method signatures of a @UserInterface-annotated
     * Grails service will be represented inside the feature model to focus on the (FN)UI usecase.
     */
    CONNECTED_COMPONENT_GENERATE_UI
}
