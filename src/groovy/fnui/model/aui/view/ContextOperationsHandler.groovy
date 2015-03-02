package fnui.model.aui.view

import fnui.model.AuiNode
import fnui.model.aui.AuiLinkUtils
import fnui.model.aui.AuiNodeUtils
import fnui.model.task.Task
import fnui.model.task.TaskFlow

class ContextOperationsHandler extends AbstractViewHandler {

    @Override
    String getHandle() {
        'ContextOperations'
    }

    @Override
    boolean handle(AuiNode viewNode, TaskFlow flow, Task task) {
        def contextLinks = AuiLinkUtils.getLinksForContext(flow, flow.viewModel)
        AuiNodeUtils.addContextOperations(viewNode, flow.viewModelVarName, contextLinks)
        return true
    }
}
