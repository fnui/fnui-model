package fnui.model.task.modeler.translators.list

import fnui.model.functionality.Functionality
import fnui.model.task.Task
import fnui.model.task.TaskFlow
import fnui.model.task.TaskFlowResult
import fnui.model.task.TaskKind
import fnui.model.task.modeler.translators.AbstractFunctionalityTranslator

/**
 * Translates a Functionality of type 'List' to a taskFlow.
 */
class ListFunctionalityTranslator extends AbstractFunctionalityTranslator {

    @Override
    String getName() {
        'List'
    }

    @Override
    boolean accepts(Functionality functionality) {
        functionality.functionalityType == 'List'
    }

    @Override
    TaskFlow doTranslation(TaskFlow taskFlow) {
        def fn = taskFlow.functionality

        def listTask = new Task()
        listTask.with {
            name = taskFlow.name
            taskType = 'List'
            taskKind = TaskKind.APPLICATION

            fn.inputParameter.each { name, functionalityParameter ->
                parameters.put(name, functionalityParameter.type)
            }
            parameters.putAll(fn.contextNeeded)
            viewModel[fn.viewModelVarName] = fn.viewModel
        }

        taskFlow.addTask(listTask)
        taskFlow.taskFlowResult = TaskFlowResult.RENDER_VIEW

        return taskFlow
    }
}
