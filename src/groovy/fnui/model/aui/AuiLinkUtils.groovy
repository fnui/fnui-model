package fnui.model.aui

import fnui.feature.model.TypeFeature
import fnui.model.AuiLink
import fnui.model.task.Task
import fnui.model.task.TaskFlow
import fnui.model.task.TaskFlowLink
import groovy.util.logging.Log4j

/**
 * The AuiLinkUtils provides construction helper for link descriptions.
 */
@Log4j
abstract class AuiLinkUtils {
    final static String DEFAULT_NAMESPACE = 'generated'

    static AuiLink getLinkByType(TaskFlow taskFlow, String type) {
        createLink(taskFlow.outgoingFlowLinks.find { it.to.flowType == type })
    }

    static AuiLink getLinkByName(TaskFlow taskFlow, String name) {
        createLink(taskFlow.outgoingFlowLinks.find { it.to.name == name })
    }

    static AuiLink getLinkByTypeAndContextClass(TaskFlow taskFlow, String type, TypeFeature contextClass) {
        createLink(getFlowLinksForContext(taskFlow, contextClass).find { it.to.flowType == type })
    }

    static Map<TypeFeature, AuiLink> getShowLinks(TaskFlow taskFlow) {
        taskFlow.outgoingFlowLinks.findAll { flowLink ->
            def to = flowLink.to
            if (to.flowType != 'Show') {
                return false
            }

            return flowLink.neededContext.size() == 1
        }.collectEntries { flowLink ->
            def viewModelType = flowLink.from.viewModel.describedType
            def contextType = flowLink.neededContext.values().first()
            def auiLink = createLink(flowLink)

            if (viewModelType != contextType) {
                [(viewModelType): auiLink, (contextType):auiLink]
            } else {
                [(viewModelType): auiLink]
            }
        }
    }

    static Map<String, AuiLink> getLinksForContext(TaskFlow taskFlow, TypeFeature contextClass = null) {
        def links = [:]
        getFlowLinksForContext(taskFlow, contextClass).collect { link ->
            def auiLink = createLink(link)
            links[auiLink.name] = auiLink
        }
        return links
    }

    static Collection<TaskFlowLink> getFlowLinksForContext(TaskFlow taskFlow, TypeFeature contextClass) {
        if (contextClass) {
            taskFlow.outgoingFlowLinks.findAll { flowLink ->
                def neededContext = flowLink.neededContext

                if (neededContext.size() != 1) {
                    return false
                }

                def neededType = flowLink.from.viewModel//neededContext.values().first()

                return neededType.isAssignableFrom(contextClass)
            }
        } else {
            taskFlow.outgoingFlowLinks.findAll { flowLink ->
                flowLink.neededContext.size() == 0
            }
        }
    }

    static AuiLink createLink(TaskFlowLink flowLink) {
        if (!flowLink) {
            return null
        }

        new AuiLink('action', initParams(flowLink), { varName ->
            if (!flowLink.neededContext) {
                return ''
            }

            if (flowLink.neededContext.size() != flowLink.fromProvidedContext.size()) {
                log.error "Defined AuiLink for $flowLink but could not provide neededContext. Generated link will be invalid in UI."
                return ''
            }


            def mapping = flowLink.fromProvidedContext.collect { name, contextParameter ->
                contextParameter.contextPath ?
                        "'${name}.id':${varName}?.${contextParameter.contextPath}?.id" :
                        "'${name}.id':${varName}?.id"
            }.join(', ')

            return "[${mapping}]"
        })
    }

    static AuiLink getDefaultLink(TaskFlow taskFlow) {
        def defaultFlow = taskFlow.taskGroup.defaultFlow
        defaultFlow ? createSimpleLink(defaultFlow) : createRootLink()
    }

    static AuiLink createSimpleLink(TaskFlow taskFlow) {
        new AuiLink('action', initParams(taskFlow))
    }

    static AuiLink createSimpleLink(Task task) {
        new AuiLink('action', initParams(task))
    }

    static AuiLink createRootLink() {
        new AuiLink('uri', [name: 'app.name.home', uri: '/'])
    }

    private static Map<String, Object> initParams(TaskFlowLink taskFlowLink) {
        def from = taskFlowLink.from
        def to = taskFlowLink.to

        def params = [name: to.firstTaskName.capitalize(), action: to.firstTaskName, namespace: DEFAULT_NAMESPACE, uiDefinition: to.uiDefintion]
        if (from.taskGroup != to.taskGroup) {
            params['controller'] = to.taskGroup.simpleControllerName
        }

        def requirements = taskFlowLink.requirements
        if (requirements) {
            params['requirements'] = requirements
        }

        return params
    }

    private static Map<String, Object> initParams(TaskFlow taskFlow) {
        [name: taskFlow.firstTaskName, action: taskFlow.firstTaskName, namespace: DEFAULT_NAMESPACE, uiDefinition: taskFlow.uiDefintion]
    }

    private static Map<String, Object> initParams(Task task) {
        [name: task.name, action: task.name, namespace: DEFAULT_NAMESPACE, uiDefinition: task.taskFlow.uiDefintion]
    }
}
