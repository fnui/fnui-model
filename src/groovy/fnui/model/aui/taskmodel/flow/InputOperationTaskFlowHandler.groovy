package fnui.model.aui.taskmodel.flow

import fnui.model.AuiNode
import fnui.model.aui.AuiLinkUtils
import fnui.model.task.Task
import fnui.model.task.TaskFlow
import fnui.util.FnuiConventions
import groovy.util.logging.Log4j

import static fnui.model.aui.AuiNodeUtils.*

@Log4j
class InputOperationTaskFlowHandler extends AbstractTaskFlowHandler {
    final String handledFlowType = 'Operation'

    @Override
    boolean accepts(AuiNode actionsNode, TaskFlow taskFlow) {
        taskFlow.taskCount == 2
    }

    @Override
    boolean handle(AuiNode actionsNode, TaskFlow taskFlow) {
        assert taskFlow.functionality.methodFeature.parameters.size() == 2

        def operationTask = taskFlow.firstTask
        def processOperationTask = taskFlow.lastTask

        addOperationTask(actionsNode, taskFlow, operationTask, processOperationTask)
        addProcessOperationTask(actionsNode, taskFlow, operationTask, processOperationTask)

        return true
    }

    void addOperationTask(AuiNode node, TaskFlow taskFlow, Task operationTask, Task processOperationTask) {
        def actionNode = addAction(node, operationTask)
        addActionParameter(actionNode, operationTask)

        def actionBody = addActionBody(actionNode)

        addNotNullValidation(actionBody, operationTask, AuiLinkUtils.getDefaultLink(taskFlow))

        def contextVar = operationTask.parameters.keySet().first()
        def inputEntry = processOperationTask.parameters.find { name, value ->
            name != contextVar
        }

        addConstructNew(actionBody, inputEntry.key, inputEntry.value)
        addRenderView(actionBody, operationTask.name, operationTask, inputEntry.key)
        addFormView(actionNode, taskFlow, operationTask, processOperationTask, 'Operation')
    }

    void addProcessOperationTask(AuiNode node, TaskFlow taskFlow, Task operationTask, Task processOperationTask) {
        def actionNode = addAction(node, processOperationTask)
        addActionParameter(actionNode, processOperationTask)

        def actionBody = addActionBody(actionNode)

        addInputValidation(actionBody, processOperationTask, operationTask)

        registry.handle('ServiceCall', actionBody, [taskFlow: taskFlow, task: processOperationTask, previousTask: operationTask])

        addViewModelHandling(actionBody, taskFlow, processOperationTask)
    }
}

