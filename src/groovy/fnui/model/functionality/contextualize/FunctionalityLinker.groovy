package fnui.model.functionality.contextualize

import fnui.model.functionality.FunctionalityModel

/**
 * Adds links between functionalities of the funcitonalityModel
 */
interface FunctionalityLinker {

    /**
     * Perform the linking described by this linker for the provided functionalityModel
     * @param functionalityModel
     */
    void link(FunctionalityModel functionalityModel)
}