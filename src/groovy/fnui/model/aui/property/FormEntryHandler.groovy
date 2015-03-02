package fnui.model.aui.property

import fnui.feature.model.ClassFeature
import fnui.feature.model.PropertyFeature
import fnui.model.AuiNode
import fnui.model.aui.AuiNodeUtils
import fnui.util.constraints.Constraints

class FormEntryHandler extends AbstractPropertyHandler {
    @Override
    String getHandle() {
        'FormEntry'
    }

    @Override
    boolean handle(AuiNode node, Map<String, Object> context) {
        ClassFeature classFeature = node['class']
        PropertyFeature property = context.propertyFeature

        AuiNodeUtils.addFormEntry(node, property, getPrepareConstraints(property))

        return true
    }

    Map<String,Object> getPrepareConstraints(PropertyFeature propertyFeature) {
        Constraints constraints = propertyFeature.constraints.clone()

        boolean shouldBeHidden = constraints.getBoolean('identifier', false) || constraints.getBoolean('version', false)
        boolean containsForm = constraints.containsKey('form')

        constraints.required = !propertyFeature.metadata['optional']

        // set form widget
        if (shouldBeHidden) {
            constraints.form = 'hiddenField'
        } else if (!containsForm) {
            if (isBooleanProperty(propertyFeature)) {
                constraints.form = 'checkbox'
            } else if (isNumberProperty(propertyFeature)) {
                constraints.form = 'number'
            } else if (isDateProperty(propertyFeature)) {
                constraints.form = 'date'
            } else if (isEnumProperty(propertyFeature)) {
                constraints.form = 'enum'
            } else if (isOneToMany(propertyFeature)) {
                constraints.form = 'oneToMany'
            } else if (isManyToOne(propertyFeature)) {
                constraints.form = 'manyToOne'
            } else if (isManyToMany(propertyFeature)) {
                constraints.form = 'manyToMany'
            } else {
                constraints.form = 'plain'
            }
        }

        return constraints
    }
}
