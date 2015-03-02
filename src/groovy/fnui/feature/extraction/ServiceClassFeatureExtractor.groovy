package fnui.feature.extraction

import fnui.feature.model.ClassFeature

/**
 * ClassFeatureExtractor for Service classes
 */
class ServiceClassFeatureExtractor extends AbstractClassFeatureExtractor {
    static final String SERVICE_EXTRACTOR = 'Service'
    static final List<String> BASIC_SERVICE_PROPERTIES = BASIC_PROPERTIES + 'transactionManager'

    @Override
    String getHandles() {
        SERVICE_EXTRACTOR
    }

    @Override
    ClassFeature getFeature(Class clazz, String artefactType) {
        def feature = newClassFeature(clazz, artefactType)

        extractBasicInfos(clazz, feature)
        extractMethodFeatures(clazz, feature)
        extractPropertyFeatures(clazz, feature, BASIC_SERVICE_PROPERTIES)

        return feature
    }
}
