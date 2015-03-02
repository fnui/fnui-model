package fnui.model.aui.taskmodel.flow

import fnui.model.AuiNode
import fnui.model.task.Task
import fnui.model.task.TaskFlow

import static fnui.model.aui.AuiNodeUtils.*

class CreateTaskFlowHandler extends AbstractTaskFlowHandler {
    final String handledFlowType  = 'Create'

    @Override
    boolean accepts(AuiNode actionsNode, TaskFlow taskFlow) {
        taskFlow.taskCount == 2
    }

    @Override
    boolean handle(AuiNode actionsNode, TaskFlow taskFlow) {
        def createTask = taskFlow.firstTask
        def processCreateTask = taskFlow.lastTask

        addCreateTask(actionsNode, taskFlow, createTask, processCreateTask)
        addProcessCreateTask(actionsNode, taskFlow, createTask, processCreateTask)

        return true
    }

    void addCreateTask(AuiNode node, TaskFlow taskFlow, Task createTask, Task processCreateTask) {
        def actionNode = addAction(node, createTask)
        def actionBody = addActionBody(actionNode)

        def viewModelEntry = getFirstViewModelEntry(createTask)

        addConstructNew(actionBody, viewModelEntry.key, viewModelEntry.value)

        addRenderView(actionBody, createTask.name, createTask, viewModelEntry.key)
        addFormView(actionNode, taskFlow, createTask, processCreateTask, 'Create')
    }

    void addProcessCreateTask(AuiNode node, TaskFlow taskFlow, Task createTask, Task processCreateTask) {
        def actionNode = addAction(node, processCreateTask)
        addActionParameter(actionNode, processCreateTask)

        def actionBody = addActionBody(actionNode)

        addInputValidation(actionBody, processCreateTask, createTask)

        registry.handle('ServiceCall', actionBody, [taskFlow:taskFlow, task:processCreateTask, previousTask:createTask])

        addViewModelHandling(actionBody, taskFlow, processCreateTask)
    }
}