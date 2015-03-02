package fnui.model.aui.taskmodel.flow

import fnui.core.viewmodel.ListViewModel
import fnui.model.AuiNode
import fnui.model.aui.AuiLinkUtils
import fnui.model.task.Task
import fnui.model.task.TaskFlow

import static fnui.model.aui.AuiNodeUtils.*

class PaginatedListTaskFlowHandler extends AbstractTaskFlowHandler {
    final String handledFlowType  = 'List'

    @Override
    boolean accepts(AuiNode actionsNode, TaskFlow taskFlow) {
        taskFlow.taskCount == 1 && taskFlow.viewModel.isAssignableTo(ListViewModel)
    }

    @Override
    boolean handle(AuiNode actionsNode, TaskFlow taskFlow) {
        def listTask = taskFlow.firstTask
        def fn = taskFlow.functionality

        def actionNode = addAction(actionsNode, listTask)
        addActionParameter(actionNode, listTask)

        def actionBody = addActionBody(actionNode)

        registry.handle('ServiceCall', actionBody, [taskFlow:taskFlow, task:listTask, noErrorHandling:true])

        addRenderView(actionBody, actionNode.name, listTask, fn.viewModelVarName)
        addPaginatedListView(actionNode, taskFlow, listTask)

        return true
    }

    void addPaginatedListView(AuiNode actionNode, TaskFlow taskFlow, Task task) {
        def viewMain = prepareBasicView(actionNode, taskFlow, task)

        def listLinks = AuiLinkUtils.getLinksForContext(taskFlow, taskFlow.viewModel.describedType)
        def simpleList = addPaginatedList(viewMain, taskFlow, listLinks)
        registry.handle('ListProperties', simpleList, [:])
    }
}