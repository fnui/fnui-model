package fnui.model.functionality.contextualize.linkers

import fnui.feature.model.TypeFeature
import fnui.model.functionality.Functionality
import fnui.model.functionality.FunctionalityContextUtils
import fnui.model.functionality.FunctionalityGroup
import fnui.model.functionality.FunctionalityModel
import fnui.model.functionality.contextualize.ContextParameter
import fnui.model.functionality.contextualize.FunctionalityLinker

/**
 * AbstractFunctionalityLinker contains common util methods used by multiple linkers.
 */
abstract class AbstractFunctionalityLinker implements FunctionalityLinker {

    @Override
    void link(FunctionalityModel fnModel) {
        fnModel.eachFunctionality { fn ->
            if (accepts(fn)) {
                linkFunctionality(fnModel, fn)
            }
        }
    }

    /**
     * Checks if the linker should be used for the provided Functionality.
     *
     * @param fn
     * @return true if usable
     */
    abstract boolean accepts(Functionality fn)

    /**
     * Adds links for the provided functionality in the model.
     *
     * @param fnModel
     * @param fn
     */
    abstract void linkFunctionality(FunctionalityModel fnModel, Functionality fn)

    /**
     * Find all functionalities in the model which can satisfy the provided context.
     *
     * @param fnModel
     * @param context mapping to be satisfied
     * @param explicitOnly if true only explicitly as context defined properties are used
     * @return
     */
    protected static Map<Functionality, Map<String, ContextParameter>> findAllWithContext(FunctionalityModel fnModel, Map<String,TypeFeature> context, boolean explicitOnly = true) {
        def matchingFunctionalities = [:]

        fnModel.eachFunctionality { fn ->
            def providedContext = FunctionalityContextUtils.getProvidedContext(fn, 1, explicitOnly)
            def providableOfNeededContext = providedContext.getProvidableContextParameter(context)

            boolean matchingFromFunctionality = context.size() == providableOfNeededContext.size()

            if (matchingFromFunctionality) {
                matchingFunctionalities[fn] = providableOfNeededContext
            }
        }

        return matchingFunctionalities
    }

    /**
     * Find all functionalities in the group which can satisfy the provided context.
     *
     * @param fnGroup
     * @param context mapping to be satisfied
     * @param explicitOnly if true only explicitly as context defined properties are used
     * @return
     */
    protected static Map<Functionality, Map<String, ContextParameter>> findAllInGroupWithContext(FunctionalityGroup fnGroup, Map<String,TypeFeature> context, boolean explicitOnly = true) {
        def matchingFunctionalities = [:]

        fnGroup.eachFunctionality { fn ->
            def providedContext = FunctionalityContextUtils.getProvidedContext(fn, 1, explicitOnly)
            def providableOfNeededContext = providedContext.getProvidableContextParameter(context)

            boolean matchingFromFunctionality = context.size() == providableOfNeededContext.size()

            if (matchingFromFunctionality) {
                matchingFunctionalities[fn] = providableOfNeededContext
            }
        }

        return matchingFunctionalities
    }
}
