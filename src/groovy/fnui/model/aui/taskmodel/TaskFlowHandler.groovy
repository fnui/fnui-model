package fnui.model.aui.taskmodel

import fnui.model.AuiNode
import fnui.model.aui.AbstractAuiGenerationHandler
import fnui.model.task.TaskFlow

class TaskFlowHandler extends AbstractAuiGenerationHandler {

    @Override
    String getHandle() {
        'TaskFlow'
    }

    @Override
    boolean handle(AuiNode node, Map<String, Object> context) {
        TaskFlow flow = context.taskFlow

        registry.handle("TaskFlow_${flow.flowType}", node, context)

        return true
    }
}
