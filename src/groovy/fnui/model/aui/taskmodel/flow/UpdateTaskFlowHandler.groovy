package fnui.model.aui.taskmodel.flow

import fnui.model.AuiNode
import fnui.model.aui.AuiLinkUtils
import fnui.model.task.Task
import fnui.model.task.TaskFlow

import static fnui.model.aui.AuiNodeUtils.*

class UpdateTaskFlowHandler extends AbstractTaskFlowHandler {
    final String handledFlowType  = 'Update'

    @Override
    boolean accepts(AuiNode actionsNode, TaskFlow taskFlow) {
        taskFlow.taskCount == 2
    }

    @Override
    boolean handle(AuiNode actionsNode, TaskFlow taskFlow) {
        def updateTask = taskFlow.firstTask
        def processUpdateTask = taskFlow.lastTask

        addUpdateTask(actionsNode, taskFlow, updateTask, processUpdateTask)
        addProcessUpdateTask(actionsNode, taskFlow, updateTask, processUpdateTask)

        return true
    }

    void addUpdateTask(AuiNode node, TaskFlow taskFlow, Task updateTask, Task processUpdateTask) {
        def actionNode = addAction(node, updateTask)
        addActionParameter(actionNode, updateTask)

        def actionBody = addActionBody(actionNode)

        addNotNullValidation(actionBody, updateTask, AuiLinkUtils.getDefaultLink(taskFlow))

        addRenderView(actionBody, updateTask.name, updateTask, getFirstParameterEntry(updateTask).key)
        addFormView(actionNode, taskFlow, updateTask, processUpdateTask, 'Update')
    }

    void addProcessUpdateTask(AuiNode node, TaskFlow taskFlow, Task updateTask, Task processUpdateTask) {
        def actionNode = addAction(node, processUpdateTask)
        addActionParameter(actionNode, processUpdateTask)

        def actionBody = addActionBody(actionNode)

        addInputValidation(actionBody, processUpdateTask, updateTask)

        registry.handle('ServiceCall', actionBody, [taskFlow:taskFlow, task:processUpdateTask, previousTask:updateTask])

        addViewModelHandling(actionBody, taskFlow, processUpdateTask)
    }
}
