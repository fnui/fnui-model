package fnui.model.functionality.contextualize.linkers

import fnui.model.functionality.Functionality
import fnui.model.functionality.FunctionalityLink
import fnui.model.functionality.FunctionalityLinkKind
import fnui.model.functionality.FunctionalityModel

/**
 * Links functionalities of type 'Show'.
 */
class ShowFunctionalityLinker extends AbstractFunctionalityLinker {

    @Override
    boolean accepts(Functionality fn) {
        fn.functionalityType == 'Show'
    }

    @Override
    void linkFunctionality(FunctionalityModel fnModel, Functionality fn) {
        def contextNeeded = fn.contextNeeded

        if (contextNeeded) {
            findAllWithContext(fnModel, contextNeeded, false).each { candidateFn, providedContext ->
                if (candidateFn == fn) { return }

                def link = new FunctionalityLink()
                link.from = candidateFn
                link.to = fn
                link.neededContext.putAll(fn.contextNeeded)
                link.fromProvidedContext.putAll(providedContext)

                boolean isExplicit = providedContext.every { _, contextParameter ->
                    contextParameter.explicitContext
                }

                link.linkKind = isExplicit ? FunctionalityLinkKind.EXPLICIT_CONTEXT_LINK : FunctionalityLinkKind.IMPLICIT_CONTEXT_LINK

                fnModel.addFunctionalityLink(link)
            }
        } else {
            // set group-links for members
            fn.group.eachFunctionality { groupFn ->
                if (groupFn == fn) { return }

                def link = new FunctionalityLink()
                link.linkKind = FunctionalityLinkKind.GROUP_LINK
                link.from = groupFn
                link.to = fn

                fnModel.addFunctionalityLink(link)
            }
        }
    }
}
