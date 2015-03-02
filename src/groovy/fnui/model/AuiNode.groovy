package fnui.model

/**
 * AuiNodes builds a tree structure which describes the UI for the generator.
 */
class AuiNode {
    String name
    String type
    Map<String,Object> data = [:]

    AuiNode parent
    List<AuiNode> children = []

    /**
     * If the node is optional and the UI generator doesn't know the node, than it can
     * ignore it and process try to process the underlying nodes.
     */
    Boolean optional = false

    /**
     * @return the first child of this node
     */
    AuiNode getChild() {
        children.first()
    }

    /**
     * @return the deepest child by traversing always the first child
     */
    AuiNode getDeepestChild() {
        def c = getChild()

        c ? c.getDeepestChild() : this
    }

    /**
     * Find the first child with the provided type.
     *
     * @param type
     * @return
     */
    AuiNode findByType(String type) {
        children.find { c -> c.type == type }
    }

    /**
     * Find all children with the provided type.
     *
     * @param type
     * @return
     */
    Collection<AuiNode> findAllByType(String type) {
        children.findAll { c -> c.type == type }
    }

    /**
     * Adds a child to this note an sets parentship.
     *
     * @param child
     */
    void addChild(AuiNode child) {
        child.parent = this
        children << child
    }

    /**
     * @return true if node has no children
     */
    boolean isEmpty() {
        children.isEmpty() && data.isEmpty()
    }

    def getAt(String dataKey) {
        data[dataKey]
    }
}
