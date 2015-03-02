package fnui.model.aui.taskmodel

import fnui.model.AuiNode
import fnui.model.aui.AbstractAuiGenerationHandler
import fnui.model.aui.AuiNodeUtils
import fnui.model.task.TaskGroup

class TaskFlowsHandler extends AbstractAuiGenerationHandler {

    @Override
    String getHandle() {
        'TaskFlows'
    }

    @Override
    boolean handle(AuiNode node, Map<String, Object> context) {
        TaskGroup group = context.taskGroup

        def actionsNode = AuiNodeUtils.addActions(node)

        for (def flow:group.taskFlowCollection.sort { a, b -> a.name <=> b.name }) {
            registry.handle('TaskFlow', actionsNode, [taskFlow:flow])
        }

        return true
    }
}
