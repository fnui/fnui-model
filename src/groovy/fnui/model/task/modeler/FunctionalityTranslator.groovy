package fnui.model.task.modeler

import fnui.model.functionality.Functionality
import fnui.model.task.TaskFlow

/**
 * A FunctionalityTranslator implementations translates a Functionality to a matching TaskFlow.
 */
interface FunctionalityTranslator {
    /**
     * The translator name is used for identifying the translator and the translator should
     * set this name as name on returned taskflows.
     *
     * @return name for the translator
     */
    String getName()

    /**
     * Checks if the functionality could be translated into a taskflow by this translator
     *
     * @param functionality
     * @return true if translatable
     */
    boolean accepts(Functionality functionality)

    /**
     * Translate a functionality if it is acceptable for the translator.
     *
     * @param functionality method for which to generator a taskflow
     * @return the new taskflow if translation was possible else null
     */
    TaskFlow translate(Functionality functionality)
}