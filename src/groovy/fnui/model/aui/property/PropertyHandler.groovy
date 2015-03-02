package fnui.model.aui.property

import fnui.feature.model.ClassFeature
import fnui.feature.model.PropertyFeature
import fnui.model.AuiNode
import fnui.model.aui.AuiNodeUtils

class PropertyHandler extends AbstractPropertyHandler {
    @Override
    String getHandle() {
        'Property'
    }

    @Override
    boolean handle(AuiNode node, Map<String, Object> context) {
        ClassFeature classFeature = node['class']
        PropertyFeature property = context.propertyFeature

        AuiNodeUtils.addProperty(node, property, getPrepareConstraints(property))

        return true
    }

    Map<String, Object> getPrepareConstraints(PropertyFeature propertyFeature) {
        Map<String, Object> constraints = propertyFeature.constraints.clone()

        boolean containsShow = constraints.containsKey('show')

        // set show widget
        if (!containsShow) {
            if (isBooleanProperty(propertyFeature)) {
                constraints.show = 'boolean'
            } else if (isDateProperty(propertyFeature)) {
                constraints.show = 'date'
            } else if (isOneToMany(propertyFeature)) {
                constraints.show = 'collection'
            } else if (isManyToMany(propertyFeature)) {
                constraints.show = 'collection'
            } else if (isCollection(propertyFeature)) {
                constraints.show = 'collection'
            } else {
                constraints.show = 'plain'
            }
        }

        return constraints
    }
}
