package fnui.model.functionality

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType

/**
 * The FunctionalityModel describes the functionalities provided by the application services marked by
 * the UserInterface annotation.
 */
class FunctionalityModel {
    Map<String,FunctionalityGroup> functionalityGroups = [:]
    Map<Functionality,List<FunctionalityLink>> fromFunctionalityLinks = [:].withDefault {[]}
    Map<Functionality,List<FunctionalityLink>> toFunctionalityLinks = [:].withDefault {[]}

    /**
     * Adds the FunctionalityGroup to the model.
     *
     * @param group
     */
    void addGroup(FunctionalityGroup group) {
        group.model = this
        functionalityGroups[group.name] = group
    }

    /**
     * Adds the FunctionalityLink to the model.
     * @param link
     */
    void addFunctionalityLink(FunctionalityLink link) {
        def fromList = fromFunctionalityLinks[link.from]
        def toList = toFunctionalityLinks[link.to]
        fromList.add(link)
        toList.add(link)
    }

    /**
     * @return the count of functionality groups in the model
     */
    int getFunctionalityGroupCount() {
        functionalityGroups.size()
    }

    /**
     * @return a view on the collection of FunctionalityLinks in the model
     */
    Collection<FunctionalityLink> getFunctionalityLinkCollection() {
        fromFunctionalityLinks.values().flatten()
    }

    /**
     * @return a view on the collection of FuntionalityGroups in the model
     */
    Collection<FunctionalityGroup> getFunctionalityGroupsCollection() {
        functionalityGroups.values()
    }

    /**
     * Helper for iteration all functionalities of the model
     *
     * @param closure which is called with each Functionality of the model
     */
    void eachFunctionality(@ClosureParams(value=SimpleType.class, options="fnui.model.functionality.Functionality") Closure closure) {
        for (def g:functionalityGroupsCollection) {
            for (def fn:g.functionalityCollection) {
                closure.call(fn)
            }
        }
    }
}
