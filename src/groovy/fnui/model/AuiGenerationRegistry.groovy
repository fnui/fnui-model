package fnui.model

/**
 * An AuiGenerationRegistry implementation handles the management of available AuiGenerationHandler
 * and AuiGenerationListener and offers methods to trigger handlers and sending events.
 */
interface AuiGenerationRegistry {
    /**
     * Delegates the definition of the provided node in dependence of the handle to the registered
     * handlers.
     *
     * The specific contracts differs based on the handle. See documentation.
     *
     * @param handle
     * @param node
     * @param context
     */
    void handle(String handle, AuiNode node, Map<String,Object> context)

    /**
     * Send an event to the registered listeners.
     *
     * @param event name of the event
     * @param node related node
     * @param context related context
     */
    void event(String event, AuiNode node, Map<String,Object> context)

    /**
     * Adds a new AuiGenerationHandler to this registry for usage in the model generation process.
     *
     * @param handler
     */
    void registerHandler(AuiGenerationHandler handler)

    /**
     * Adds a new AuiGenerationListener to this registry which gets notified about events in the
     * generation process.
     *
     * @param listener
     */
    void registerListener(AuiGenerationListener listener)
}