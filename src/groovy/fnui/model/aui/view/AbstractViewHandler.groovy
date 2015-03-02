package fnui.model.aui.view

import fnui.model.AuiNode
import fnui.model.aui.AbstractAuiGenerationHandler
import fnui.model.task.Task
import fnui.model.task.TaskFlow

abstract class AbstractViewHandler extends AbstractAuiGenerationHandler {

    @Override
    boolean handle(AuiNode viewNode, Map<String, Object> context) {
        TaskFlow flow = context.flow
        Task task = context.task

        return handle(viewNode, flow, task)
    }

    /**
     * The handle method modifies the provided AuiNode according the propose of the
     * handler implementation.
     *
     * @param node
     * @param flow the flow where the task belongs to
     * @param task the task for which the view will be described
     * @return
     * true if handler did everything needed, otherwise next available handler will be
     *    called with same node and context
     */
    abstract boolean handle(AuiNode viewNode, TaskFlow flow, Task task)
}
