package fnui.model.functionality.contextualize

import fnui.model.functionality.FunctionalityModel

/**
 * The FunctionalityContextualizer manage the available FunctionalityLinker and
 * allows to start the contextualization process.
 */
class FunctionalityContextualizer {

    private List<FunctionalityLinker> linkers = []

    /**
     * Add links between functionalities with help of the managed linkers.
     *
     * @param functionalityModel
     * @return
     */
    FunctionalityModel contextualize(FunctionalityModel functionalityModel) {
        for (def c:linkers) {
            c.link(functionalityModel)
        }

        return functionalityModel
    }

    void addLinker(FunctionalityLinker linker) {
        linkers << linker
    }

    void clearLinkers() {
        linkers.clear()
    }
}