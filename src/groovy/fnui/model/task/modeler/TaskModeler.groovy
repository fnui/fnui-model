package fnui.model.task.modeler

import fnui.model.functionality.Functionality
import fnui.model.functionality.FunctionalityGroup
import fnui.model.functionality.FunctionalityModel
import fnui.model.task.TaskFlow
import fnui.model.task.TaskGroup
import fnui.model.task.TaskModel
import groovy.util.logging.Log4j

/**
 * The TaskModeler creates a TaskModel for FunctionalityModel and manages the
 * FunctionalityTranslators used for this modeling process.
 */
@Log4j
class TaskModeler {

    private List<FunctionalityTranslator> translators = []

    TaskFlowConnector taskFlowConnector = new TaskFlowConnector()

    /**
     * Creates the TaskModel for the provided FunctionalityModel
     *
     * @param fnModel
     * @return
     */
    TaskModel modelTasks(FunctionalityModel fnModel) {
        log.debug 'Starting TaskModel creation process...'

        def taskModel = new TaskModel()
        taskModel.functionalityModel = fnModel

        for (def fnGroup : fnModel.functionalityGroupsCollection) {
            generateTaskGroup(taskModel, fnGroup)
        }

        taskModel.initializeFunctionalityTaskFlowMapping()

        if (taskFlowConnector) {
             return taskFlowConnector.connect(taskModel)
        }

        log.debug 'Finished TaskModel creation.'

        return taskModel
    }

    private TaskGroup generateTaskGroup(TaskModel taskModel,FunctionalityGroup fnGroup) {
        def taskGroup = new TaskGroup()
        taskGroup.name = fnGroup.name
        taskGroup.functionalityGroup = fnGroup
        taskModel.addTaskGroup(taskGroup)

        for (def fn : fnGroup.functionalityCollection) {
            def taskFlow = getTaskFlowFor(fn)
            if (taskFlow) {
                taskGroup.addTaskFlow(taskFlow)
            }
        }

        return taskGroup
    }

    private TaskFlow getTaskFlowFor(Functionality functionality) {
        def translator = null
        for (def t : translators) {
            if (t.accepts(functionality)) {
                translator = t; break
            }
        }

        if (!translator) {
            log.info "No functionality translator for functionality: ${functionality}"
            return null
        }

        def taskFlow = translator.translate(functionality)

        if (!taskFlow) {
            log.info "FunctionalityTranslator ${translator.name} didn't translate functionality: ${functionality}"
        }

        return taskFlow
    }

    /**
     * Adds a translator to the list which is check one by one if it can handle a method definition
     * and the first which accepts the message is used.
     *
     * The before-relation is only considered while adding a new translator:
     * - [A,C] => add(B,[C]) => [A,B,C]
     * - [A] => add(B,[C]) => [A,B] => add(C,[A]) => [C,A,B]
     *
     * @param translator translator, which should be added
     * @param before defines a list of translators which should be after this if they exists already
     */
    void addTranslator(FunctionalityTranslator translator, List<String> before = []) {
        int minIndex = translators.size()

        for (def b : before) {
            for (int i = 0; i < translators.size(); i++) {
                if (translators[i].name == b) {
                    minIndex = Math.min(minIndex, i)
                }
            }
        }

        translators.add(minIndex, translator)
    }

    void clearTranslatorList() {
        translators.clear()
    }
}

