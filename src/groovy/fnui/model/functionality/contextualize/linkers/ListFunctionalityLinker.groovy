package fnui.model.functionality.contextualize.linkers

import fnui.model.functionality.Functionality
import fnui.model.functionality.FunctionalityLink
import fnui.model.functionality.FunctionalityLinkKind
import fnui.model.functionality.FunctionalityModel
import groovy.util.logging.Log4j

/**
 * Links functionalities of type 'List'.
 */
@Log4j
class ListFunctionalityLinker extends AbstractFunctionalityLinker {

    @Override
    boolean accepts(Functionality fn) {
        fn.functionalityType == 'List'
    }

    @Override
    void linkFunctionality(FunctionalityModel fnModel, Functionality fn) {
        def contextNeeded = fn.contextNeeded

        if (contextNeeded) {
            log.warn "Can not link functionality ${fn.serviceVarName}.${fn.serviceMethodName} of type List because it needs a context"
        } else {
            // Group-links for group members
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
