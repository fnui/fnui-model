package fnui.model
/**
 * An AuiGenerationHandler instance handles certain concerns in the AUI generation process.
 */
interface AuiGenerationHandler {
    /**
     * @return the identifier for identifying the concern of the handler instance
     */
    String getHandle()

    /**
     * The handle method modifies the provided AuiNode according the propose of the
     * handler implementation. The AuiGenerationHandlerRegistry selects the appropriated
     * handlers by the defined handle.
     *
     * @param node
     * @param context can contain anything which is needed for the specific handler task
     * @return
     *    true if handler did everything needed, otherwise next available handler will be
     *    called with same node and context
     */
    boolean handle(AuiNode node, Map<String, Object> context)

    void setRegistry(AuiGenerationRegistry registry)
}
