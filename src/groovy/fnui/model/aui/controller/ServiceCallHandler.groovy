package fnui.model.aui.controller

import fnui.feature.model.TypeFeature
import fnui.model.AuiNode
import fnui.model.aui.AbstractAuiGenerationHandler
import fnui.model.aui.AuiLinkUtils
import fnui.model.definition.UserInterfaceDefinition
import fnui.model.task.Task
import fnui.model.task.TaskFlow
import fnui.util.FnuiFeatureUtils

import static fnui.model.aui.AuiNodeUtils.*

class ServiceCallHandler extends AbstractAuiGenerationHandler {

    @Override
    String getHandle() {
        'ServiceCall'
    }

    @Override
    boolean handle(AuiNode actionBody, Map<String, Object> context) {
        TaskFlow taskFlow = context.taskFlow
        Task task = context.task
        Task previousTask = context.previousTask
        boolean noErrorHandling = context.noErrorHandling

        assert taskFlow && task

        def uiDef = taskFlow.uiDefintion
        boolean serviceAnnounceException = uiDef.get('exception')

        if (taskFlow.viewModel.isAssignableTo(void)) {
            handleVoidResult(noErrorHandling, actionBody, taskFlow, uiDef, task, previousTask)
        } else if (noErrorHandling) {
            addServiceCallWithVar(actionBody, taskFlow)
        } else if (serviceAnnounceException) {
            withExceptionHandling(actionBody, taskFlow, uiDef, task, previousTask)
        } else {
            withoutExceptionHandling(actionBody, taskFlow, uiDef, task, previousTask)
        }

        return true
    }

    private static withoutExceptionHandling(AuiNode actionBody, TaskFlow taskFlow, UserInterfaceDefinition uiDef, Task task, Task previousTask) {
        taskFlow.viewModelVarName

        addServiceCallWithVar(actionBody, taskFlow)

        addBasicServiceResultHandling(actionBody, taskFlow, uiDef, task, previousTask)
    }

    private static withExceptionHandling(AuiNode actionBody, TaskFlow taskFlow, UserInterfaceDefinition uiDef, Task task, Task previousTask) {

        addViewModelDeclaration(actionBody, taskFlow)

        def exceptionValue = uiDef.get('exception')

        TypeFeature exceptionType = null
        if (exceptionValue instanceof Class) {
            exceptionType = new TypeFeature(taskFlow.functionality.methodFeature.classFeature.featureModel, exceptionValue)
        }

        def exceptionHandling = addServiceExceptionHandling(actionBody, exceptionType)

        addErrorTargetCall(exceptionHandling, uiDef, taskFlow, task, previousTask)

        addBasicServiceResultHandling(exceptionHandling, taskFlow, uiDef, task, previousTask)
    }

    private static addBasicServiceResultHandling(AuiNode actionBody, TaskFlow taskFlow, UserInterfaceDefinition uiDef, Task task, Task previousTask) {
        def validationNode
        if (FnuiFeatureUtils.isValidateable(taskFlow.viewModel)) {
            validationNode = addHasErrorsServiceValidation(actionBody, taskFlow.viewModelVarName)
            addErrorPipe(validationNode, taskFlow.viewModelVarName)
        } else {
            validationNode = addNotNullServiceValidation(actionBody, taskFlow.viewModelVarName)
        }

        addErrorTargetCall(validationNode, uiDef, taskFlow, task, previousTask)

        addServiceSuccessMessage(actionBody, uiDef)
    }

    private static addErrorTargetCall(AuiNode parentNode, UserInterfaceDefinition uiDef, TaskFlow taskFlow, Task task, Task previousTask) {
        addServiceErrorMessage(parentNode, uiDef)

        if (previousTask) {
            addRenderView(parentNode, previousTask.name, task)
        } else {
            addRedirect(parentNode, AuiLinkUtils.getDefaultLink(taskFlow), null)
        }

        addReturn(parentNode)
    }

    private static handleVoidResult(boolean noErrorHandling, AuiNode actionBody, TaskFlow taskFlow, UserInterfaceDefinition uiDef, Task task, Task previousTask) {
        def parentNode = actionBody

        def exceptionValue = uiDef.get('exception')
        if (!noErrorHandling && exceptionValue) {
            TypeFeature exceptionType = null
            if (exceptionValue instanceof Class) {
                exceptionType = new TypeFeature(taskFlow.functionality.methodFeature.classFeature.featureModel, exceptionValue)
            }

            def exceptionHandling = addServiceExceptionHandling(actionBody, exceptionType)

            addErrorTargetCall(exceptionHandling, uiDef, taskFlow, task, previousTask)

            addBasicServiceResultHandling(exceptionHandling, taskFlow, uiDef, task, previousTask)

        }

        addServiceCallWithoutResult(parentNode, taskFlow)
        addServiceSuccessMessage(actionBody, uiDef)
    }
}
