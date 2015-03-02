package fnui.model.aui

import fnui.feature.model.PropertyFeature
import fnui.feature.model.TypeFeature
import fnui.model.AuiLink
import fnui.model.AuiNode
import fnui.model.definition.UserInterfaceDefinition
import fnui.model.task.Task
import fnui.model.task.TaskFlow
import fnui.model.task.TaskGroup
import fnui.util.FnuiConventions
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType

/**
 * The AuiNodeUtils provides construction helper for the AuiNodes.
 */
abstract class AuiNodeUtils {

    static AuiNode addProperties(AuiNode parentNode) {
        addNodeTo(parentNode) {
            type = 'properties'
            data = parentNode.data
        }
    }

    static AuiNode addProperty(AuiNode parentNode, PropertyFeature propertyFeature, Map<String,Object> constraints) {
        Map<TypeFeature,AuiLink> showLinks = parentNode['showLinks']

        def showLink = null
        if (constraints['identifier']) {
            TypeFeature contextType = parentNode['type']
            showLink = showLinks[contextType]
        } else {
            showLink = showLinks[propertyFeature.type]
        }

        addNodeTo(parentNode) {
            type = 'property'
            name = propertyFeature.name
            data['type'] = propertyFeature.type
            data['feature'] = propertyFeature
            data['constraints'] = constraints
            if (showLink) {
                data['link'] = showLink
            }
        }
    }

    static AuiNode addFormEntry(AuiNode parentNode, PropertyFeature propertyFeature, Map<String,Object> constraints) {
        addNodeTo(parentNode) {
            type = 'formEntry'
            name = propertyFeature.name
            data['type'] = propertyFeature.type
            data['feature'] = propertyFeature
            data['constraints'] = constraints
        }
    }

    static AuiNode addServiceDeclaration(AuiNode parentNode, TaskGroup taskGroup) {
        addNodeTo(parentNode) {
            type = 'serviceDeclaration'
            name = taskGroup.name
            data['type'] = taskGroup.serviceType
            data['varName'] = taskGroup.serviceVarName
        }
    }

    static AuiNode addNotNullValidation(AuiNode parentNode, Task task, AuiLink link) {
        def parameters = new ArrayList<>(task.parameters.keySet())

        if (!parameters) {
            return null
        }

        def validation = addValidationHelper('notNull', parentNode, parameters)
        addErrorMessage(validation, '', 'unknown.entity')
        addRedirect(validation, link, null)
        addReturn(validation)

        return validation
    }

    static AuiNode addInputValidation(AuiNode parentNode, Task task, Task beforeTask) {
        def parameters = task.validatableParameter

        if (!parameters) {
            return null
        }

        def validation = addValidationHelper('hasErrors', parentNode, task.validatableParameter)
        addRenderView(validation, beforeTask.name, task)
        addReturn(validation)

        return validation
    }

    static AuiNode addMessage(AuiNode parentNode, String defaultMessage, String i18nCode) {
        addMessageHelper('message', parentNode, defaultMessage, i18nCode)
    }

    static AuiNode addWarningMessage(AuiNode parentNode, String defaultMessage, String i18nCode) {
        addMessageHelper('warning', parentNode, defaultMessage, i18nCode)
    }

    static AuiNode addErrorMessage(AuiNode parentNode, String defaultMessage, String i18nCode) {
        addMessageHelper('error', parentNode, defaultMessage, i18nCode)
    }

    static AuiNode addErrorPipe(AuiNode parentNode, String varName) {
        addNodeTo(parentNode) {
            type = 'errorPipe'
            data['varName'] = varName
        }
    }

    static AuiNode addRedirect(AuiNode parentNode, AuiLink redirectLink, String parameterVarName = null) {
        addNodeTo(parentNode) {
            type = 'redirect'
            if (parameterVarName) {
                data['parameterVar'] = parameterVarName
            }
            data['target'] = redirectLink
        }
    }

    static AuiNode addReturn(AuiNode parentNode) {
        addNodeTo(parentNode) {
            type = 'return'
        }
    }

    static void addActionParameter(AuiNode parentNode, Task task) {
        task.parameters.each { parameterName, type ->
            addParameter(parentNode, parameterName, type)
        }
    }

    static AuiNode addParameter(AuiNode parentNode, String parameterName, TypeFeature parameterType) {
        addNodeTo(parentNode) {
            type = 'parameter'
            name = parameterName
            data['type'] = parameterType
            data['varName'] = parameterName
        }
    }

    static AuiNode addViewModelDeclaration(AuiNode parentNode, TaskFlow flow) {
        addNodeTo(parentNode) {
            type = 'viewModelDeclaration'
            data['type'] = flow.viewModel
            data['varName'] = flow.viewModelVarName
        }
    }

    static AuiNode addServiceCall(AuiNode parentNode, TaskFlow flow) {
        def fn = flow.functionality
        def serviceCall = addNodeTo(parentNode) {
            type = 'serviceCall'
            data['serviceVarName'] = fn.serviceVarName
            data['methodName'] = fn.serviceMethodName
            data['varName'] = flow.viewModelVarName
        }

        def method = fn.methodFeature
        method.parameterOrder.each { parameterName ->
            def pf = method.parameters[parameterName]
            addParameter(serviceCall, parameterName, pf.type)
        }

        return serviceCall
    }

    static AuiNode addServiceCallWithoutResult(AuiNode parentNode, TaskFlow flow) {
        def serviceCall = addServiceCall(parentNode, flow)
        serviceCall.data['varName'] = null

        return serviceCall
    }

    static AuiNode addServiceCallWithVar(AuiNode parentNode, TaskFlow flow) {
        def serviceCall = addServiceCall(parentNode, flow)
        serviceCall.data['type'] = flow.viewModel

        return serviceCall
    }

    static AuiNode addNotNullServiceValidation(AuiNode parentNode, String viewModelVarName) {
        addValidationHelper('notNull', parentNode, [viewModelVarName])
    }

    static AuiNode addHasErrorsServiceValidation(AuiNode parentNode, String viewModelVarName) {
        addValidationHelper('notNullHasErrors', parentNode, [viewModelVarName])
    }

    static AuiNode addServiceExceptionHandling(AuiNode parentNode, TypeFeature exceptionType) {
        def exceptionHandling = addNodeTo(parentNode) {
            type = 'exceptionHandling'
            if (exceptionType) {
                data['exceptionType'] = exceptionType
            }
        }

        return exceptionHandling
    }

    static AuiNode addServiceErrorMessage(AuiNode parentNode, UserInterfaceDefinition uiDef) {
        String errorMessage = uiDef.get('errorMessage')
        String errorMessageI18n = uiDef.get('errorMessageI18n')

        if (errorMessage || errorMessageI18n) {
            return addErrorMessage(parentNode, errorMessage, errorMessageI18n)
        } else {
            return addErrorMessage(parentNode, 'generic.error', null)
        }
    }

    static AuiNode addServiceSuccessMessage(AuiNode parentNode, UserInterfaceDefinition uiDef) {
        String message = uiDef.get('message')
        String messageI18n = uiDef.get('messageI18n')

        if (message || messageI18n) {
            return addMessage(parentNode, message, messageI18n)
        }
    }

//    static AuiNode addRenderView(AuiNode parentNode, String viewName, Task task, String viewModelVar) {
//        def viewModelName = task.viewModel.keySet().first()
//        addNodeTo(parentNode) {
//            type = 'renderView'
//            data['viewName'] = viewName
//            data['model'] = "[${viewModelName}:${viewModelVar}]"
//        }
//    }

    static AuiNode addRenderView(AuiNode parentNode, String viewName, Task task, String inputVarName = null) {
        Map<String,String> vmDef = [:]
        task.parameters.each { name, type ->
            def viewVarName = FnuiConventions.getVarName(type, 'Instance')
            vmDef.put(viewVarName, name)
        }

        if (inputVarName) {
            vmDef.put(inputVarName, inputVarName)
        }

        addNodeTo(parentNode) {
            type = 'renderView'
            data['viewName'] = viewName
            data['model'] = vmDef.toString()
        }
    }

    static AuiNode addView(AuiNode actionNode) {
        addNodeTo(actionNode) {
            type = 'view'
            name = actionNode.name
        }
    }

    static AuiNode addActionBody(AuiNode actionNode) {
        addNodeTo(actionNode) {
            type = 'actionBody'
        }
    }

    static AuiNode addAction(AuiNode actionsNode, Task task) {
        addNodeTo(actionsNode) {
            type = 'action'
            name = task.name
            data['uiDefinition'] = task.taskFlow.uiDefintion
        }
    }

    static AuiNode addActions(AuiNode parentNode) {
        addNodeTo(parentNode) {
            type = 'actions'
        }
    }

    static AuiNode addViewPartMain(AuiNode viewNode) {
        addNodeTo(viewNode) {
            type = 'main'
        }
    }

    static AuiNode addDetailsListing(AuiNode viewNode, TaskFlow flow) {
        addNodeTo(viewNode) {
            type = 'detailsListing'
            data['varName'] = flow.viewModelVarName
            data['type'] = flow.viewModel
            data['class'] = flow.viewModel.feature
            data['showLinks'] = AuiLinkUtils.getShowLinks(flow)
        }
    }

    static AuiNode addSimpleList(AuiNode viewNode, TaskFlow flow, Map<String,AuiLink> links) {
        addNodeTo(viewNode) {
            type = 'simpleList'
            data['varName'] = flow.viewModelVarName
            data['type'] = flow.viewModel.describedType
            data['class'] = flow.viewModel.describedType.feature
            data['links'] = links
            data['showLinks'] = AuiLinkUtils.getShowLinks(flow)
        }
    }

    static AuiNode addPaginatedList(AuiNode viewNode, TaskFlow flow, Map<String,AuiLink> links) {
        addNodeTo(viewNode) {
            type = 'paginatedList'
            data['varName'] = flow.viewModelVarName
            data['type'] = flow.viewModel.describedType
            data['class'] = flow.viewModel.describedType.feature
            data['links'] = links
            data['pagination'] = AuiLinkUtils.createSimpleLink(flow)
            data['showLinks'] = AuiLinkUtils.getShowLinks(flow)
        }
    }

    static AuiNode addForm(AuiNode viewNode, TaskFlow flow, Map<String,AuiLink> links, AuiLink submitLink) {
        addNodeTo(viewNode) {
            type = 'form'
//            data['targetVarName'] = flow.lastTask.parameters.keySet().first()
//            data['varName'] = flow.viewModelVarName
//            data['type'] = flow.viewModel
//            data['class'] = flow.viewModel.feature
            data['defaultSubmit'] = submitLink
            data['links'] = links
        }
    }

    static AuiNode addFormObject(AuiNode formNode, String varName, String targetVarName, TypeFeature typeFeature) {
        addNodeTo(formNode) {
            type = 'formObject'
            data['varName'] = varName
            data['targetVarName'] = targetVarName
            data['type'] = typeFeature
            data['class'] = typeFeature.feature
        }
    }

    static AuiNode addConstructNew(AuiNode parentNode, String varName, TypeFeature typeFeature) {
        addNodeTo(parentNode) {
            type = 'constructNew'
            data['varName'] = varName
            data['type'] = typeFeature
        }
    }

    static AuiNode addOperationsBar(AuiNode viewNode, Map<String,AuiLink> links) {
        if (!links) {
            return null
        }

        def operationsBar = addNodeTo(viewNode) {
            type = 'operationsBar'
        }

        links.each { name, link ->
            addLink(operationsBar, link)
        }

        return operationsBar
    }

    static AuiNode addContextOperations(AuiNode viewNode, String varName, Map<String,AuiLink> links) {
        if (!links) {
            return null
        }

        def contextOperations = addNodeTo(viewNode) {
            type = 'contextOperations'
            data['contextVar'] = varName
        }

        links.each {name, link ->
            addLink(contextOperations, link)
        }

        return contextOperations
    }

    static AuiNode addLink(AuiNode operationsBar, AuiLink link) {
        addNodeTo(operationsBar) {
            type = 'link'
            name = link.name
            data['details'] = link
        }
    }

    private static AuiNode addValidationHelper(String kind, AuiNode parentNode, List<String> validatables) {
        addNodeTo(parentNode) {
            type = 'validation'
            data['kind'] = kind
            data['validatables'] = validatables
        }
    }

    static AuiNode addContextBox(AuiNode viewNode) {
        addNodeTo(viewNode) {
            type = 'contextBox'
        }
    }

    private static AuiNode addMessageHelper(String level, AuiNode parentNode, String defaultMessage, String i18nCode) {
        addNodeTo(parentNode) {
            type = 'message'
            data['level'] = level
            data['kind'] = i18nCode ? 'i18n' : 'plain'
            data['i18nCode'] = i18nCode
            data['message'] = defaultMessage
        }
    }

    private static AuiNode addNodeTo(AuiNode parentNode, @DelegatesTo(AuiNode) @ClosureParams(value=SimpleType.class, options="fnui.model.AuiNode") Closure closure) {
        def node = new AuiNode()

        def clonedClosure = (Closure) closure.clone()
        clonedClosure.setResolveStrategy(Closure.DELEGATE_FIRST)
        clonedClosure.setDelegate(node)
        clonedClosure.call(node)
        parentNode.addChild(node)

        return node
    }
}
