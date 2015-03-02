package fnui.model.task.modeler.translators.operator

import fnui.model.functionality.Functionality
import fnui.model.task.Task
import fnui.model.task.TaskFlow
import fnui.model.task.TaskFlowResult
import fnui.model.task.TaskKind
import fnui.model.task.modeler.translators.AbstractFunctionalityTranslator
import groovy.util.logging.Log4j

/**
 * Translates a Functionality of type 'Operation' to a taskFlow.
 */
@Log4j
class SimpleDomainFunctionalityTranslator extends AbstractFunctionalityTranslator {

    @Override
    String getName() {
        'Operation'
    }

    @Override
    boolean accepts(Functionality functionality) {
        functionality.functionalityType == 'Operation' && !functionality.inputParameter
    }

    @Override
    TaskFlow doTranslation(TaskFlow taskFlow) {
        def fn = taskFlow.functionality

        def operationTask = new Task()
        operationTask.with {
            name = taskFlow.name
            taskType = 'ProcessOperation'
            taskKind = TaskKind.APPLICATION

            parameters.putAll(fn.contextNeeded)
            viewModel[fn.viewModelVarName] = fn.viewModel
        }

        taskFlow.addTask(operationTask)

        def showLink = findShowLink(taskFlow, fn.viewModel)
        if (showLink) {
            taskFlow.taskFlowResult = TaskFlowResult.REDIRECT_SHOW
            taskFlow.resultFunctionalityLink = showLink
        } else if ( fn.viewModel.isAssignableTo(boolean) || fn.viewModel.isAssignableTo(void)) {
            taskFlow.taskFlowResult = TaskFlowResult.REDIRECT_DEFAULT
        } else {
            taskFlow.taskFlowResult = TaskFlowResult.RENDER_VIEW
        }

        return taskFlow
    }
}
