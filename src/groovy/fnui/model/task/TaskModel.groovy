package fnui.model.task
import fnui.model.functionality.Functionality
import fnui.model.functionality.FunctionalityModel
/**
 * The task model describes the tasks which should be available in the UI. Therefore the model
 * contains taskFlows which describes the needed steps to execute an functionality.
 */
class TaskModel {
    FunctionalityModel functionalityModel
    Map<TaskFlow,List<TaskFlowLink>> fromTaskFlowLinks = [:].withDefault {[]}
    Map<TaskFlow,List<TaskFlowLink>> toTaskFlowLinks = [:].withDefault {[]}

    Map<Functionality,TaskFlow> functionalityTaskFlowMap = [:]

    Map<String, TaskGroup> taskGroups = [:]

    /**
     * Adds a taskGroup to the model and connects it correctly.
     *
     * @param taskGroup
     */
    void addTaskGroup(TaskGroup taskGroup) {
        taskGroup.taskModel = this
        taskGroups[taskGroup.name] = taskGroup
    }

    /**
     * Adds a taskFlow link to the model and connects it correctly.
     *
     * @param link
     */
    void addTaskFlowLink(TaskFlowLink link) {
        def fromList = fromTaskFlowLinks[link.from]
        def toList = toTaskFlowLinks[link.to]
        fromList.add(link)
        toList.add(link)
    }

    /**
     * Collects all taskflow from the underlying structure and initialize the
     * functionalityTaskFlowMap with them.
     */
    void initializeFunctionalityTaskFlowMapping() {
        for (def g:taskGroupCollection) {
            for (def taskFlow:g.taskFlowCollection) {
                functionalityTaskFlowMap[taskFlow.functionality] = taskFlow
            }
        }
    }

    /**
     * @return the count of contained taskGroups
     */
    int getTaskGroupCount() {
        taskGroups.size()
    }

    /**
     * @return a view on the taskGroup mapping values
     */
    Collection<TaskGroup> getTaskGroupCollection() {
        taskGroups.values()
    }

    /**
     * @return a view in the taskFlow-links mapping values
     */
    Collection<TaskFlowLink> getTaskFlowLinkCollection() {
        fromTaskFlowLinks.values().flatten()
    }
}
