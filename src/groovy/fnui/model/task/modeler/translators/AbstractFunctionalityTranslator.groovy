package fnui.model.task.modeler.translators
import fnui.feature.model.TypeFeature
import fnui.model.functionality.Functionality
import fnui.model.functionality.FunctionalityLink
import fnui.model.task.TaskFlow
import fnui.model.task.modeler.FunctionalityTranslator
import groovy.util.logging.Log4j

/**
 * AbstractFunctionalityTranslator provides common functions for the translation.
 */
@Log4j
abstract class AbstractFunctionalityTranslator implements FunctionalityTranslator {

    @Override
    TaskFlow translate(Functionality functionality) {
        if (!accepts(functionality)) {
            return null
        }

        def taskFlow = new TaskFlow()
        taskFlow.name = functionality.name
        taskFlow.functionality = functionality
        taskFlow.flowType = getName()

        return doTranslation(taskFlow)
    }

    /**
     * Executes the functionalityType specific transformations to the prepared taskFlow instance.
     *
     * Preparations:
     *  - name
     *  - functionality
     *  - and flowType are set
     *
     * @param taskFlow
     * @return
     */
    abstract TaskFlow doTranslation(TaskFlow taskFlow)

    protected static FunctionalityLink findShowLink(TaskFlow taskFlow, TypeFeature typeFeature) {
        log.debug "Looking for 'show' functionality for ${typeFeature}..."
        log.debug "Available links ${taskFlow.functionality.outGoingLinks}"

        taskFlow.functionality.outGoingLinks.find { link ->
            link.to.functionalityType == 'Show' && link.matchesContext([typeFeature])
        }
    }

    protected static FunctionalityLink findDefaultLink(TaskFlow taskFlow) {
        def defaultFunctionality = taskFlow.taskGroup.defaultFlow.functionality
        taskFlow.functionality.outGoingLinks.find { link ->
            link.to == defaultFunctionality
        }
    }
}
