package fnui.feature.extraction

import fnui.feature.model.ClassFeature

/**
 * ClassFeatureExtractor for Domain classes
 */
class TagLibClassFeatureExtractor extends AbstractClassFeatureExtractor {
    static final String TAGLIB_EXTRACTOR = 'TagLib'
    static final List<String> BASIC_TAGLIB_PROPERTIES = ['class', 'metaClass', 'properties', 'out',
                                                         'pageScope', 'params', 'session', 'request',
                                                         'response', 'servletContext', 'grailsApplication']

    @Override
    String getHandles() {
        TAGLIB_EXTRACTOR
    }

    @Override
    ClassFeature getFeature(Class clazz, String artefactType) {
        def feature = newClassFeature(clazz, artefactType)

        extractBasicInfos(clazz, feature)
        extractMethodFeatures(clazz, feature)
        extractPropertyFeatures(clazz, feature, BASIC_TAGLIB_PROPERTIES)

        return feature
    }
}
