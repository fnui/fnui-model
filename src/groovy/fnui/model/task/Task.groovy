package fnui.model.task

import fnui.feature.model.TypeFeature
import fnui.util.FnuiFeatureUtils
import groovy.transform.ToString

/**
 * A Task describes a single user interface step and is part of an TaskFlow.
 * Examples:
 *  - present data
 *  - present a form
 *  - process form data
 */
@ToString(excludes = ['taskFlow'], includeNames = true)
class Task {
    /**
     * The name for a task. Should be unique for an tastGroup
     */
    String name

    /**
     * The valid values of taskType depends on the available features in the later generation phases.
     */
    String taskType

    /**
     * Defines the kind of the task-user-interaction
     */
    TaskKind taskKind

    /**
     * The name of the next task step.
     */
    String nextTaskName

    /**
     * The taskflow this task is part of.
     */
    TaskFlow taskFlow

    /**
     * Parameters for this task
     */
    Map<String,TypeFeature> parameters = [:]

    /**
     * The viewModel definition for this task.
     */
    Map<String,TypeFeature> viewModel = [:]

    /**
     * @return the names of validateable input parameters. The determination of validateability is deferred to
     *         {@link fnui.util.FnuiFeatureUtils#isValidateable(fnui.feature.model.TypeFeature)}
     */
    List<String> getValidatableParameter() {
        def params = []

        parameters.each {name, type ->
            if (FnuiFeatureUtils.isValidateable(type)) {
                params << name
            }
        }

        return params
    }
}
