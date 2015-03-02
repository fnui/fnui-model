package fnui.model.aui.taskmodel.flow

import fnui.model.AuiNode
import fnui.model.aui.AuiLinkUtils
import fnui.model.task.TaskFlow
import groovy.util.logging.Log4j

import static fnui.model.aui.AuiNodeUtils.*

@Log4j
class SimpleOperationTaskFlowHandler extends AbstractTaskFlowHandler {
    final String handledFlowType  = 'Operation'

    @Override
    boolean accepts(AuiNode actionsNode, TaskFlow taskFlow) {
        taskFlow.taskCount == 1
    }

    @Override
    boolean handle(AuiNode actionsNode, TaskFlow taskFlow) {
        def operationTask = taskFlow.firstTask

        def actionNode = addAction(actionsNode, operationTask)
        addActionParameter(actionNode, operationTask)

        def actionBody = addActionBody(actionNode)

        addNotNullValidation(actionBody, operationTask, AuiLinkUtils.getDefaultLink(taskFlow))

        registry.handle('ServiceCall', actionBody, [taskFlow:taskFlow, task:operationTask])

        addViewModelHandling(actionBody, taskFlow, operationTask)

        return true
    }
}