package fnui.model.aui.taskmodel

import fnui.model.AuiNode
import fnui.model.aui.AbstractAuiGenerationHandler
import fnui.model.aui.AuiNodeUtils as ANU
import fnui.model.task.TaskGroup

class TaskGroupHandler extends AbstractAuiGenerationHandler {

    @Override
    String getHandle() {
        'TaskGroup'
    }

    @Override
    boolean handle(AuiNode node, Map<String, Object> context) {
        TaskGroup group = context.taskGroup
        node.name = group.controllerName
        node.type = 'controller'

        node.data['servicePackage'] = group.servicePackageName
        node.data['simpleName'] = group.simpleControllerName
        node.data['uiDefinition'] = group.uiDefintion

        def defaultAction = group.defaultFlow?.firstTaskName
        if (defaultAction) {
            node.data['defaultAction'] = defaultAction
        }

        ANU.addServiceDeclaration(node, group)

        registry.handle('TaskFlows', node, [taskGroup:group])

        return true
    }
}
