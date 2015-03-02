package fnui.model.task

import groovy.transform.CompileStatic

/**
 * Defines the kind of the task-user-interaction. Currently it is primarily defined for inspection proposes.
 */
@CompileStatic
enum TaskKind {
    /**
     * Task expects user input
     */
    INTERACTIVE,
    /**
     * Task works also without user input
     */
    APPLICATION
}
