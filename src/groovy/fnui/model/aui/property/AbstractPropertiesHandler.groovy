package fnui.model.aui.property

import fnui.feature.model.ClassFeature
import fnui.feature.model.PropertyFeature
import fnui.model.aui.AbstractAuiGenerationHandler
import fnui.util.FnuiFeatureUtils

abstract class AbstractPropertiesHandler extends AbstractAuiGenerationHandler {

    static List<PropertyFeature> getBasicPropertyOrder(ClassFeature classFeature) {
        def propertyList = classFeature.propertyOrder.collect { propertyFeature ->
            classFeature.propertyFeatures[propertyFeature]
        }.findAll { propertyFeature ->
            def constraints = propertyFeature.constraints
            !constraints.getBoolean('version', false) && constraints.getBoolean('display', true)
        }

        if (FnuiFeatureUtils.isDomainObject(classFeature)) {
            int indexOfIndex = propertyList.findIndexOf { propertyFeature ->
                propertyFeature.constraints.getBoolean('identifier', false)
            }

            def idProperty = propertyList.remove(indexOfIndex)
            return [idProperty] + propertyList
        }

        if (FnuiFeatureUtils.isCommandObject(classFeature)) {
            int indexOfErrors = propertyList.findIndexOf { propertyFeature ->
                propertyFeature.name == 'errors'
            }

            propertyList.remove(indexOfErrors)
            return propertyList
        }

        return propertyList
    }
}
