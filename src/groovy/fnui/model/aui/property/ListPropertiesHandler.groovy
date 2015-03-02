package fnui.model.aui.property

import fnui.feature.model.ClassFeature
import fnui.feature.model.PropertyFeature
import fnui.model.AuiNode
import fnui.model.aui.AuiNodeUtils

class ListPropertiesHandler extends AbstractPropertiesHandler {
    final static Integer MAX_COLUMNS = 5

    @Override
    String getHandle() {
        'ListProperties'
    }

    @Override
    boolean handle(AuiNode listNode, Map<String, Object> context) {
        ClassFeature classFeature = listNode['class']

        def selectedProperties = filterProperties(getBasicPropertyOrder(classFeature))

        if (!selectedProperties) {
            return true
        }

        def propertiesNode = AuiNodeUtils.addProperties(listNode)
        selectedProperties.each { propertyFeature ->
            registry.handle('Property', propertiesNode, [propertyFeature:propertyFeature])
        }

        return true
    }

    List<PropertyFeature> filterProperties(List<PropertyFeature> props) {
        def listProperties = props.findAll { propertyFeature ->
            propertyFeature.constraints.getBoolean('displayList', true)
        }

        int countAvailableColumns = listProperties.size()
        if (!countAvailableColumns) {
            return []
        }

        def lastSelectedIndex = Math.min(countAvailableColumns, MAX_COLUMNS) - 1

        return listProperties[0..lastSelectedIndex]
    }
}
