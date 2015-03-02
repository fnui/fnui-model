package fnui.model.task.modeler.translators.update

import fnui.model.functionality.Functionality
import fnui.model.task.Task
import fnui.model.task.TaskFlow
import fnui.model.task.TaskFlowResult
import fnui.model.task.TaskKind
import fnui.model.task.modeler.translators.AbstractFunctionalityTranslator

/**
 * Translates a Functionality of type 'Update' to a taskFlow.
 */
class UpdateFunctionalityTranslator extends AbstractFunctionalityTranslator {

    @Override
    String getName() {
        'Update'
    }

    @Override
    boolean accepts(Functionality functionality) {
        functionality.functionalityType == 'Update'
    }

    @Override
    TaskFlow doTranslation(TaskFlow taskFlow) {
        def fn = taskFlow.functionality

        def updateTask = new Task()
        updateTask.with {
            name = taskFlow.name
            taskType = 'Update'
            taskKind = TaskKind.INTERACTIVE

            fn.inputParameter.each { name, functionalityParameter ->
                parameters.put(name, functionalityParameter.type)
            }
            parameters.putAll(fn.contextNeeded)
            viewModel[fn.viewModelVarName] = fn.viewModel
        }

        def processUpdateTask = new Task()
        processUpdateTask.with {
            name = "process${updateTask.name.capitalize()}"
            taskType = 'ProcessUpdate'
            taskKind = TaskKind.APPLICATION

            parameters.putAll(updateTask.parameters)
            viewModel[fn.viewModelVarName] = fn.viewModel
        }

        updateTask.nextTaskName = processUpdateTask.name

        taskFlow.addTask(updateTask)
        taskFlow.addTask(processUpdateTask)

        def showLink = findShowLink(taskFlow, fn.viewModel)
        if (showLink) {
            taskFlow.taskFlowResult = TaskFlowResult.REDIRECT_SHOW
            taskFlow.resultFunctionalityLink = showLink
        } else {
            taskFlow.taskFlowResult = TaskFlowResult.RENDER_VIEW
        }

        return taskFlow
    }
}
