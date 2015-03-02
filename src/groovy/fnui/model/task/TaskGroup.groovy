package fnui.model.task

import fnui.feature.model.TypeFeature
import fnui.model.definition.UserInterfaceDefinition
import fnui.model.functionality.FunctionalityGroup
import fnui.util.FnuiConventions

/**
 * TaskGroups represent logical collections of TaskFlows defined by the FunctionalityGroups of the
 * FunctionalityModel and therefore they are directly related to a certain service.
 *
 * A taskGroup describes furthermore a controller like used in Grails.
 */
class TaskGroup {
    String name

    Map<String,TaskFlow> taskFlows = [:]

    FunctionalityGroup functionalityGroup

    TaskModel taskModel

    void addTaskFlow(TaskFlow taskFlow) {
        taskFlow.taskGroup = this
        taskFlows[taskFlow.name] = taskFlow
    }

    UserInterfaceDefinition getUiDefintion() {
        functionalityGroup.uiDefinition
    }

    /**
     * @return the conventional controller name for this task group
     */
    String getControllerName() {
        FnuiConventions.getControllerNameForService(functionalityGroup.serviceClassFeature)
    }

    /**
     * @return the simple name for the controller according to the conventions
     */
    String getSimpleControllerName() {
        FnuiConventions.getSimpleControllerNameForService(functionalityGroup.serviceClassFeature)
    }

    /**
     * @return the package name of the related service
     */
    String getServicePackageName() {
        functionalityGroup.packageName
    }

    /**
     * @return the typeFeature for the related service
     */
    TypeFeature getServiceType() {
        functionalityGroup.serviceType
    }

    /**
     * @return the convention variable name for the service like used for dependency injection
     */
    String getServiceVarName() {
        functionalityGroup.serviceVarName
    }

    /**
     * Find the default flow for this taskGroup
     *
     * @return
     */
    TaskFlow getDefaultFlow() {
        def listFlow = taskFlows.find { name, flow ->
            flow.flowType == 'List' && flow.functionality.contextNeeded.isEmpty()
        }

        return listFlow?.value ?: taskFlows.find { name, flow ->
            flow.flowType == 'Show' && flow.functionality.contextNeeded.isEmpty()
        }?.value

    }

    /**
     * @return a view to the taskFlow mapping values
     */
    Collection<TaskFlow> getTaskFlowCollection() {
        taskFlows.values()
    }
}
