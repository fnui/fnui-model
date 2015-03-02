package fnui.model.task

import fnui.feature.model.TypeFeature
import fnui.model.definition.RequirementsDescription
import fnui.model.definition.UserInterfaceDefinition
import fnui.model.functionality.Functionality
import fnui.model.functionality.FunctionalityLink

/**
 * A taskFlow describes the steps to execute a Functionality
 */
class TaskFlow {
    String name

    /**
     * The flowType is used for categorization of similar TaskFlow. It is normally the same as
     * the related functionalityType
     */
    String flowType

    /**
     * The taskFlowResult describes how the result of the taskFlow (=Functionality result) should be presented.
     */
    TaskFlowResult taskFlowResult

    /**
     * The TaskFlowLink resultLink
     */
    FunctionalityLink resultFunctionalityLink

    /**
     * The entry point task
     */
    String firstTaskName
    Map<String,Task> tasks = [:]

    Functionality functionality

    TaskGroup taskGroup

    /**
     * Adds the provided task to the flow and connects it correctly.
     *
     * If firstTaskName is not defined it will be set to the task's name.
     *
     * @param task
     */
    void addTask(Task task) {
        task.taskFlow = this
        tasks[task.name] = task

        if (!firstTaskName) {
            firstTaskName = task.name
        }
    }

    /**
     * @return the task defined by firstTaskName
     */
    Task getFirstTask() {
        tasks[firstTaskName]
    }

    /**
     * @return the last task in the chain of tasks reachable from the first task
     */
    Task getLastTask() {
        def t = firstTask
        while (t.nextTaskName) {
            t = tasks[t.nextTaskName]
        }
        return t
    }

    UserInterfaceDefinition getUiDefintion() {
        functionality.uiDefinition
    }

    List<TaskFlowLink> getOutgoingFlowLinks() {
        taskGroup.taskModel.fromTaskFlowLinks[this]
    }

    String toString() {
        "TaskFlow '${name}': ${tasks}"
    }

    /**
     * @return a view to the task mapping values
     */
    Collection<Task> getTaskCollection() {
        tasks.values()
    }

    TaskFlowLink getResultLink() {
        taskGroup.taskModel.taskFlowLinkCollection.find { taskFlowLink ->
            taskFlowLink.functionalityLink == resultFunctionalityLink
        }
    }

    String getViewModelVarName() {
        functionality.viewModelVarName
    }

    TypeFeature getViewModel() {
        functionality.viewModel
    }

    int getTaskCount() {
        tasks.size()
    }

    RequirementsDescription getRequirements() {
        functionality.requirements
    }
}
