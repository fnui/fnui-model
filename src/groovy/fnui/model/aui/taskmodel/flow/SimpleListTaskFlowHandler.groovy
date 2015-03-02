package fnui.model.aui.taskmodel.flow

import fnui.model.AuiNode
import fnui.model.aui.AuiLinkUtils
import fnui.model.task.Task
import fnui.model.task.TaskFlow

import static fnui.model.aui.AuiNodeUtils.*

class SimpleListTaskFlowHandler extends AbstractTaskFlowHandler {
    final String handledFlowType  = 'List'

    @Override
    boolean accepts(AuiNode actionsNode, TaskFlow taskFlow) {
        taskFlow.taskCount == 1
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
        addSimpleListView(actionNode, taskFlow, listTask)

        return true
    }

    void addSimpleListView(AuiNode actionNode, TaskFlow taskFlow, Task task) {
        def viewMain = prepareBasicView(actionNode, taskFlow, task)

        def listLinks = AuiLinkUtils.getLinksForContext(taskFlow, taskFlow.viewModel.describedType)
        def simpleList = addSimpleList(viewMain, taskFlow, listLinks)
        registry.handle('ListProperties', simpleList, [:])
    }
}