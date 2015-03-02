package fnui.model.aui.taskmodel.flow

import fnui.model.AuiNode
import fnui.model.aui.AuiLinkUtils
import fnui.model.task.TaskFlow

import static fnui.model.aui.AuiNodeUtils.*

class ShowTaskFlowHandler extends AbstractTaskFlowHandler {
    final String handledFlowType  = 'Show'

    @Override
    boolean accepts(AuiNode actionsNode, TaskFlow taskFlow) {
        taskFlow.taskCount == 1
    }

    @Override
    boolean handle(AuiNode actionsNode, TaskFlow taskFlow) {
        def showTask = taskFlow.firstTask

        def actionNode = addAction(actionsNode, showTask)
        addActionParameter(actionNode, showTask)

        def actionBody = addActionBody(actionNode)
        addNotNullValidation(actionBody, showTask, AuiLinkUtils.getDefaultLink(taskFlow))

        registry.handle('ServiceCall', actionBody, [taskFlow:taskFlow, task:showTask])

        addViewModelHandling(actionBody, taskFlow, showTask)

        return true
    }
}

