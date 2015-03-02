package fnui.model

/**
 * An AuiGenerationListener waits for a defined event in the AUI generation process.
 */
interface AuiGenerationListener {
    /**
     * @return the name of the wanted event
     */
    String getEventName()

    void onEvent(AuiNode, Map<String, Object> context)

    /**
     * If an event with the defined name occurs the onEvent-method is called with the related
     * node an context.
     *
     * @param node
     * @param context contains additional data for event processing
     */
    boolean onEvent(AuiNode node, Map<String, Object> context)

    void setRegistry(AuiGenerationRegistry registry)
}
