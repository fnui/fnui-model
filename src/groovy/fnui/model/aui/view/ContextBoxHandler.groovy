package fnui.model.aui.view

import fnui.model.AuiNode
import fnui.model.aui.AuiNodeUtils
import fnui.model.task.Task
import fnui.model.task.TaskFlow

class ContextBoxHandler extends AbstractViewHandler {

    @Override
    String getHandle() {
        'ContextBox'
    }

    @Override
    boolean handle(AuiNode viewNode, TaskFlow flow, Task task) {
        def contextBox = AuiNodeUtils.addContextBox(viewNode)

        //TODO: describe context

        return true
    }
}
