package fnui.model.aui.property

import fnui.feature.model.ClassFeature
import fnui.model.AuiNode
import fnui.model.aui.AuiNodeUtils

class DetailsPropertiesHandler extends AbstractPropertiesHandler {

    @Override
    String getHandle() {
        'DetailsProperties'
    }

    @Override
    boolean handle(AuiNode detailsNode, Map<String, Object> context) {
        ClassFeature classFeature = detailsNode['class']

        def selectedProperties = getBasicPropertyOrder(classFeature)

        if (!selectedProperties) {
            return true
        }

        def propertiesNode = AuiNodeUtils.addProperties(detailsNode)
        selectedProperties.each { propertyFeature ->
            registry.handle('Property', propertiesNode, [propertyFeature:propertyFeature])
        }

        return true
    }
}
