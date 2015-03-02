package fnui.model

import fnui.feature.model.FeatureModel
import fnui.model.functionality.modeler.FunctionalityModeler
import fnui.model.task.TaskModel
import fnui.model.task.modeler.TaskModeler
import groovy.util.logging.Log4j

/**
 * The UiModelGenerator is the central instance for the AbstractUserInterfaceModel generation
 * base on an provided FeatureModel.
 */
@Log4j
class UiModelGenerator implements AuiGenerationRegistry {
    /**
     * Reference to the underlying used FunctionalityModeler which generates the FunctionalityModel
     * which is used in the AuiModel generation.
     */
    FunctionalityModeler functionalityModeler

    /**
     * Reference to the underlying used TaskModeler which generates the TaskModel which is used in
     * the AuiModel generation.
     */
    TaskModeler taskModeler

    Map<String,List<AuiGenerationHandler>> handlerMap = [:].withDefault {[]}

    Map<String,List<AuiGenerationListener>> listenerMap = [:].withDefault {[]}

    /**
     * Generates an AuiModel based on the provided FeatureModel.
     *
     * Pre-requirement: A FunctionalityModeler and TaskModeler has to be set on this instance.
     *
     * @param featureModel
     * @return
     */
    AbstractUserInterfaceModel generateModel(FeatureModel featureModel) {
        assert functionalityModeler && taskModeler
        assert featureModel

        def fnModel = functionalityModeler.modelFunctionality(featureModel)
        def taskModel = taskModeler.modelTasks(fnModel)

        return generateAui(taskModel)
    }

    private AbstractUserInterfaceModel generateAui(TaskModel taskModel) {
        def auiModel = new AbstractUserInterfaceModel()

        log.debug 'Starting AbstractUserInterfaceModel creation process...'
        for (def group:taskModel.taskGroupCollection.sort { a, b -> a.name <=> b.name }) {
            log.trace "Modeling AuiNodes for ${group.name}"
            def auiGroup = new AuiNode()
            handle('TaskGroup', auiGroup, [taskGroup:group])
            auiModel.addAuiGroup(auiGroup)
        }
        log.debug 'Finished AbstractUserInterfaceModel creation.'

        return auiModel
    }

    @Override
    void registerHandler(AuiGenerationHandler handler) {
        handler.registry = this
        handlerMap[handler.getHandle()] << handler
    }

    @Override
    void registerListener(AuiGenerationListener listener) {
        listener.registry = this
        listenerMap[listener.getEventName()] << listener
    }

    @Override
    void handle(String handle, AuiNode node, Map<String,Object> context) {
        boolean finished = false
        for (def handler:handlerMap[handle]) {
            finished = handler.handle(node, context)

            if (finished) break
        }

        if (!finished) {
            log.info "No handler handled generation for handle '$handle' with node '${node}' and context ${context}."
        }

        event("on${handle.capitalize()}", node, context)
    }

    @Override
    void event(String event, AuiNode node, Map<String, Object> context) {
        for (def listener:listenerMap[event]) {
            listener.onEvent(node, context)
        }
    }
}
