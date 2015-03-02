package fnui.model.functionality.contextualize.linkers

import fnui.model.functionality.Functionality
import fnui.model.functionality.FunctionalityLink
import fnui.model.functionality.FunctionalityLinkKind
import fnui.model.functionality.FunctionalityModel
import groovy.util.logging.Log4j

/**
 * Links functionalities of type 'Update'.
 */
@Log4j
class UpdateFunctionalityLinker extends AbstractFunctionalityLinker {

    @Override
    boolean accepts(Functionality fn) {
        fn.functionalityType == 'Update'
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
            log.warn "Can not link functionality ${fn.serviceVarName}.${fn.serviceMethodName} of type Update because its context-free"
        }
    }
}
