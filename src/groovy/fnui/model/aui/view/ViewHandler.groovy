package fnui.model.aui.view

import fnui.model.AuiNode
import fnui.model.task.Task
import fnui.model.task.TaskFlow

import static fnui.model.aui.AuiNodeUtils.addViewPartMain

class ViewHandler extends AbstractViewHandler {

    @Override
    String getHandle() {
        'View'
    }

    @Override
    boolean handle(AuiNode viewNode, TaskFlow flow, Task task) {
        addViewPartMain(viewNode)

        def parameters = [flow:flow, task:task]
        registry.handle('ContextBox', viewNode, parameters)
        registry.handle('OperationsBar', viewNode, parameters)

        return true
    }
}
