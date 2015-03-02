package fnui.model.task.modeler.translators.show

import fnui.model.functionality.Functionality
import fnui.model.task.Task
import fnui.model.task.TaskFlow
import fnui.model.task.TaskFlowResult
import fnui.model.task.TaskKind
import fnui.model.task.modeler.translators.AbstractFunctionalityTranslator

/**
 * Translates a Functionality of type 'Show' to a taskFlow.
 */
class ShowFunctionalityTranslator extends AbstractFunctionalityTranslator {

    @Override
    String getName() {
        'Show'
    }

    @Override
    boolean accepts(Functionality functionality) {
        functionality.functionalityType == 'Show'
    }

    @Override
    TaskFlow doTranslation(TaskFlow taskFlow) {
        def fn = taskFlow.functionality

        def showTask = new Task()
        showTask.with {
            name = taskFlow.name
            taskType = 'Show'
            taskKind = TaskKind.APPLICATION

            fn.inputParameter.each { name, functionalityParameter ->
                parameters.put(name, functionalityParameter.type)
            }
            parameters.putAll(fn.contextNeeded)
            viewModel[fn.viewModelVarName] = fn.viewModel
        }

        taskFlow.addTask(showTask)
        taskFlow.taskFlowResult = TaskFlowResult.RENDER_VIEW

        return taskFlow
    }
}
