package fnui.model

/**
 * The AbstractUserInterfaceModel describes the interface in an abstract tree structure which provides the
 * necessary information for generating an concrete user interface
 */
class AbstractUserInterfaceModel {
    /**
     * AuiGroups depicts logical groupings of UI parts
     */
    Map<String,AuiNode> auiGroups = [:]

    /**
     * Adds an auiGroup to this model. An auiGroup is an AuiNode of type 'controller', which describes a set
     * of User Interface operations.
     *
     * @param auiNode
     */
    void addAuiGroup(AuiNode auiNode) {
        assert auiNode.type == 'controller'
        auiGroups[auiNode.name] = auiNode
    }

    /**
     * @return the count of AuiGroups in this model.
     */
    int getAuiGroupCount() {
        auiGroups.size()
    }

    /**
     * @return a view on the available auiGroups
     */
    Collection<AuiNode> getAuiGroupCollection() {
        auiGroups.values()
    }
}
