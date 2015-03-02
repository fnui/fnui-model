package fnui.model.aui.taskmodel.flow

import fnui.feature.model.TypeFeature
import fnui.model.AuiNode
import fnui.model.aui.AbstractAuiGenerationHandler
import fnui.model.aui.AuiLinkUtils
import fnui.model.task.Task
import fnui.model.task.TaskFlow
import fnui.model.task.TaskFlowResult
import fnui.util.FnuiConventions
import groovy.util.logging.Log4j

import static fnui.model.aui.AuiNodeUtils.*

@Log4j
abstract class AbstractTaskFlowHandler extends AbstractAuiGenerationHandler {

    @Override
    String getHandle() {
        "TaskFlow_${handledFlowType}"
    }

    /**
     * @return the flowType the TaskFlowHanlder will handle
     */
    abstract String getHandledFlowType()

    @Override
    boolean handle(AuiNode node, Map<String, Object> context) {
        TaskFlow taskFlow = context.taskFlow

        if (!taskFlow) {
            log.error "${this.getClass().name} was called to on handle ${getHandle()} but context did not provide a taskFlow"
            return false
        }

        if (!accepts(node, taskFlow)) {
            return false
        }

        return handle(node, taskFlow)
    }

    /**
     * Checks if the handler wants to handle the generation for the provided taskFlow
     *
     * @param actionsNode the actions collector node of a controller node
     * @param taskFlow which should be handled
     * @return true if handler will handle this node
     */
    abstract boolean accepts(AuiNode actionsNode, TaskFlow taskFlow)

    /**
     * Checks if the handle
     *
     * @param actionsNode the actions collector node of a controller node
     * @param taskFlow which should be handled
     * @return true, if generation is finished
     */
    abstract boolean handle(AuiNode actionsNode, TaskFlow taskFlow)

    void addViewModelHandling(AuiNode actionBody, TaskFlow flow, Task task) {
        if (flow.taskFlowResult == TaskFlowResult.REDIRECT_SHOW ) {
            addRedirect(actionBody, AuiLinkUtils.createLink(flow.resultLink), task.getViewModel()?.keySet()?.first())
        } else if (flow.taskFlowResult == TaskFlowResult.REDIRECT_DEFAULT) {
            addRedirect(actionBody, AuiLinkUtils.getDefaultLink(flow))
        } else {
            addRenderView(actionBody, task.name, task, flow.viewModelVarName)
            addDetailsView(actionBody.parent, flow, task)
        }
    }

    AuiNode prepareBasicView(AuiNode actionNode, TaskFlow taskFlow, Task task) {
        def view = addView(actionNode)

        registry.handle("View", view, [flow:taskFlow, task:task])

        return view.findByType('main')
    }

    void addDetailsView(AuiNode actionNode, TaskFlow taskFlow, Task task) {
        def main = prepareBasicView(actionNode, taskFlow, task)

        def detailsListing = addDetailsListing(main, taskFlow)
        registry.handle('DetailsProperties', detailsListing, null)
        registry.handle('ContextOperations', detailsListing, [flow:taskFlow, task:task])
    }

    void addFormView(AuiNode actionNode, TaskFlow taskFlow, Task requestTask, Task responseTask, String formMode) {
        def viewMain = prepareBasicView(actionNode, taskFlow, requestTask)

        def contextlinks = AuiLinkUtils.getLinksForContext(taskFlow, taskFlow.viewModel)
        def form = addForm(viewMain, taskFlow, contextlinks, AuiLinkUtils.createSimpleLink(responseTask))
        def fn = taskFlow.functionality

        fn.contextNeeded.each { key, type ->
            def formObject = addFormObject(form, FnuiConventions.getVarName(type, 'Instance'), key, type)
            registry.handle('FormEntries', formObject, [mode:'Context'])
        }

        fn.inputParameter.each { key, parameter ->
            def formObject = addFormObject(form, FnuiConventions.getVarName(parameter.type, 'Instance'), key,   parameter.type)
            registry.handle('FormEntries', formObject, [mode:formMode])
        }
    }

    def getFirstViewModelEntry(Task task) {
        assert task.viewModel.size() == 1
        task.viewModel.entrySet().first()
    }

    def getFirstParameterEntry(Task task) {
        assert task.parameters.size() == 1
        task.parameters.entrySet().first()
    }
}
