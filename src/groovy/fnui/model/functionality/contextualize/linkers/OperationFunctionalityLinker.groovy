package fnui.model.functionality.contextualize.linkers

import fnui.model.functionality.Functionality
import fnui.model.functionality.FunctionalityLink
import fnui.model.functionality.FunctionalityLinkKind
import fnui.model.functionality.FunctionalityModel
import groovy.util.logging.Log4j

/**
 * Links functionalities of type 'Operation'.
 */
@Log4j
class OperationFunctionalityLinker extends AbstractFunctionalityLinker {

    @Override
    boolean accepts(Functionality fn) {
        fn.functionalityType == 'Operation'
    }

    @Override
    void linkFunctionality(FunctionalityModel fnModel, Functionality fn) {
        def contextNeeded = fn.contextNeeded

        if (contextNeeded) {
            findAllInGroupWithContext(fn.group, contextNeeded).each { candidateFn, providedContext ->
                if (candidateFn == fn) { return }

                if (candidateFn.functionalityType != 'Show') { return }

                def link = new FunctionalityLink()
                link.linkKind = FunctionalityLinkKind.EXPLICIT_CONTEXT_LINK
                link.from = candidateFn
                link.to = fn
                link.neededContext.putAll(fn.contextNeeded)
                link.fromProvidedContext.putAll(providedContext)

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
