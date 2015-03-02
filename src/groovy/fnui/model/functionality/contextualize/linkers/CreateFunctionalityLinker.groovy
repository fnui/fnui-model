package fnui.model.functionality.contextualize.linkers

import fnui.model.functionality.Functionality
import fnui.model.functionality.FunctionalityLink
import fnui.model.functionality.FunctionalityLinkKind
import fnui.model.functionality.FunctionalityModel
import groovy.util.logging.Log4j

/**
 * Links functionalities of type 'Create'.
 */
@Log4j
class CreateFunctionalityLinker extends AbstractFunctionalityLinker {

    @Override
    boolean accepts(Functionality fn) {
        fn.functionalityType == 'Create'
    }

    @Override
    void linkFunctionality(FunctionalityModel fnModel, Functionality fn) {
        def contextNeeded = fn.contextNeeded

        if (contextNeeded) {
            log.warn "Can not link functionality ${fn.serviceVarName}.${fn.serviceMethodName} of type Create because it needs a context"
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
