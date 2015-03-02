package fnui.model.task.modeler

import fnui.model.task.TaskFlowLink
import fnui.model.task.TaskModel

/**
 * The TaskFlowConnector transfer the links form the FunctionalityModel
 * onto the TaskModel.
 */
class TaskFlowConnector {

    /**
     * Connects the TaskFlows of the model.
     *
     * @param taskModel
     * @return
     */
    TaskModel connect(TaskModel taskModel) {

        for (def group:taskModel.taskGroupCollection) {
            for (def flow:group.taskFlowCollection) {
                def fn = flow.functionality
                for (def fnLink:fn.outGoingLinks) {
                    def targetFlow = taskModel.functionalityTaskFlowMap[fnLink.to]

                    if (targetFlow) {
                        def link = new TaskFlowLink()
                        link.from = flow
                        link.to = targetFlow
                        link.functionalityLink = fnLink
                        taskModel.addTaskFlowLink(link)
                    }
                }
            }
        }

        return taskModel
    }
}
