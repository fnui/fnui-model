package fnui.model.aui.view

import fnui.model.AuiNode
import fnui.model.aui.AuiLinkUtils
import fnui.model.aui.AuiNodeUtils
import fnui.model.task.Task
import fnui.model.task.TaskFlow

class OperationsBarHandler extends AbstractViewHandler {

    @Override
    String getHandle() {
        'OperationsBar'
    }

    @Override
    boolean handle(AuiNode viewNode, TaskFlow flow, Task task) {
        AuiNodeUtils.addOperationsBar(viewNode, AuiLinkUtils.getLinksForContext(flow))
        return true
    }
}
