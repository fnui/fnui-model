package fnui.feature.extraction

import fnui.feature.model.ClassFeature
import groovy.util.logging.Log4j

/**
 * ClassFeatureExtractor for Groovy classes
 */
@Log4j
class GroovyClassFeatureExtractor extends AbstractClassFeatureExtractor {
    @Override
    String getHandles() {
        GROOVY_FEATURE_EXTRACTOR
    }

    final static String GROOVY_FEATURE_EXTRACTOR = 'GROOVY'

    @Override
    ClassFeature getFeature(Class clazz, String artefactType) {
        def feature = newClassFeature(clazz, artefactType)

        extractBasicInfos(clazz, feature)
        extractMethodFeatures(clazz, feature)

        def ignoredProperties = BASIC_PROPERTIES
        if (clazz.isEnum()) {
            ignoredProperties = BASIC_PROPERTIES + ['declaringClass']
        }

        extractPropertyFeatures(clazz, feature, ignoredProperties)

        return feature
    }
}
