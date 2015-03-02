package fnui.model.aui.property

import fnui.feature.model.ClassFeature
import fnui.feature.model.PropertyFeature
import fnui.model.AuiNode
import groovy.util.logging.Log4j

@Log4j
class FormEntriesHandler extends AbstractPropertiesHandler {
    final static Integer MAX_COLUMNS = 5

    @Override
    String getHandle() {
        'FormEntries'
    }

    @Override
    boolean handle(AuiNode formNode, Map<String, Object> context) {
        ClassFeature classFeature = formNode['class']
        String mode = context.mode

        def selectedProperties = filterProperties(getBasicPropertyOrder(classFeature), mode)

        if (!selectedProperties) {
            return true
        }

        selectedProperties.each { propertyFeature ->
            registry.handle('FormEntry', formNode, [mode:mode, propertyFeature:propertyFeature])
        }

        return true
    }

    List<PropertyFeature> filterProperties(List<PropertyFeature> props, String mode) {
        int countAvailableColumns = props.size()
        if (!countAvailableColumns) {
            return []
        }

        return props.findAll { propertyFeature ->
            def constraints = propertyFeature.constraints
            switch (mode) {
                case 'Create':
                    return !constraints.getBoolean('identifier', false) && !constraints.getBoolean('version', false)
                case 'Context':
                    return constraints.getBoolean('identifier', false)
                case 'Update':
                    return !constraints.getBoolean('createOnly', false) && !constraints.getBoolean('identifier', false)
                default:
                    return !constraints.getBoolean('identifier', false)
            }
        }
    }
}
