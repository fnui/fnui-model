package fnui.model.task.modeler.translators.create

import fnui.model.functionality.Functionality
import fnui.model.task.Task
import fnui.model.task.TaskFlow
import fnui.model.task.TaskFlowResult
import fnui.model.task.TaskKind
import fnui.model.task.modeler.translators.AbstractFunctionalityTranslator
import groovy.util.logging.Log4j

/**
 * Translates a Functionality of type 'Create' to a taskFlow.
 */
@Log4j
class CreateFunctionalityTranslator extends AbstractFunctionalityTranslator {

    @Override
    String getName() {
        'Create'
    }

    @Override
    boolean accepts(Functionality functionality) {
        functionality.functionalityType == 'Create'
    }

    @Override
    TaskFlow doTranslation(TaskFlow taskFlow) {
        def fn = taskFlow.functionality

        def createTask = new Task()
        createTask.with {
            name = taskFlow.name
            taskType = 'Create'
            taskKind = TaskKind.INTERACTIVE
//          TODO: Should the create allow to set values on call?
//            fn.inputParameter.each { name, functionalityParameter ->
//                parameters.put(name, functionalityParameter.type)
//            }
            parameters.putAll(fn.contextNeeded)
            viewModel[fn.viewModelVarName] = fn.viewModel
        }

        def processCreateTask = new Task()
        processCreateTask.with {
            name = "process${createTask.name.capitalize()}"
            taskType = 'ProcessCreate'
            taskKind = TaskKind.APPLICATION

            fn.inputParameter.each { name, functionalityParameter ->
                parameters.put(name, functionalityParameter.type)
            }
            viewModel[fn.viewModelVarName] = fn.viewModel
        }

        createTask.nextTaskName = processCreateTask.name

        taskFlow.addTask(createTask)
        taskFlow.addTask(processCreateTask)

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
