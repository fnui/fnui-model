package fnui.model.aui.property

import fnui.feature.model.PropertyFeature
import fnui.model.aui.AbstractAuiGenerationHandler
import fnui.util.FnuiFeatureUtils

abstract class AbstractPropertyHandler extends AbstractAuiGenerationHandler {

    static boolean isBooleanProperty(PropertyFeature propertyFeature) {
        def type = propertyFeature.type
        type.isRawClass(Boolean) || type.isRawClass(boolean)
    }

    static boolean isNumberProperty(PropertyFeature propertyFeature) {
        def type = propertyFeature.type
        type.isAssignableTo(Number) || (type.rawClass.primitive && !type.isRawClass(boolean))
    }

    static boolean isDateProperty(PropertyFeature propertyFeature) {
        def type = propertyFeature.type
        type.isAssignableTo(Date) || type.isAssignableTo(Calendar)
    }

    static boolean isEnumProperty(PropertyFeature propertyFeature) {
        def type = propertyFeature.type
        type.rawClass.isEnum()
    }

    static boolean isOneToMany(PropertyFeature propertyFeature) {
        def meta = propertyFeature.metadata
        meta['oneToMany'] && meta['bidirectional']
    }

    static boolean isManyToOne(PropertyFeature propertyFeature) {
        def meta = propertyFeature.metadata

        if (meta['association']) {
            return (meta['manyToOne'] || meta['oneToOne'])
        } else {
            return FnuiFeatureUtils.isDomainObject(propertyFeature.rawClassFeature)
        }
    }

    static boolean isManyToMany(PropertyFeature propertyFeature) {
        def meta = propertyFeature.metadata
        (meta['oneToMany'] && !meta['bidirectional']) || (meta['manyToMany'] && meta['owningSide'])
    }

    static boolean isCollection(PropertyFeature propertyFeature) {
        propertyFeature.type.isCollection()
    }
}
