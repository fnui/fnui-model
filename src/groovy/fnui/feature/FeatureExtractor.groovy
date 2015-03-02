package fnui.feature

import fnui.feature.model.FeatureModel
import fnui.feature.pipeline.ClassCollection
import fnui.feature.pipeline.FeatureExtraction
import fnui.feature.pipeline.FeatureModelFinalization
import fnui.util.DefaultClassRelevanceProvider
import fnui.util.FnuiClassUtils
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.support.aware.GrailsApplicationAware

/**
 * The FeatureExtractor is the first component of the (FN)UI toolchain and
 * collects information about the classes of the application (and plugins).
 *
 * The FeatureExtractor only consider classes for extraction which are relevant
 * according to {@link fnui.util.ClassRelevanceProvider#isClassRelevant(Class)}.
 */
@CompileStatic
@Log4j
class FeatureExtractor implements GrailsApplicationAware {
    GrailsApplication grailsApplication

    final DefaultClassRelevanceProvider classRelevanceProvider = new DefaultClassRelevanceProvider()

    ClassCollection classCollection
    FeatureExtraction featureExtraction
    FeatureModelFinalization featureModelFinalization

    void setGrailsApplication(GrailsApplication grailsApplication) {
        this.grailsApplication = grailsApplication
    }

    /**
     * Convenient initialization for the FeatureExtractor instance.
     * Does not change already set subcomponents.
     *
     * PRE-REQUIREMENT: grailsApplication should be set before call
     *
     * @param withoutSource if true, class discovery search for source data
     */
    void initializeExtractor(boolean withoutSource = false) {
        assert grailsApplication

        classCollection = classCollection ?: new ClassCollection(grailsApplication, withoutSource)
        featureExtraction = featureExtraction ?: new FeatureExtraction(grailsApplication, classRelevanceProvider)
        featureModelFinalization = featureModelFinalization ?: new FeatureModelFinalization()
    }

    FeatureModel extract(FeatureModelDetailLevel detailsLevel = FeatureModelDetailLevel.CONNECTED_COMPONENT_GENERATE_UI) {
        assert grailsApplication, 'FeatureExtractor needs a instance of grailsApplication'
        assert classCollection && featureExtraction && featureModelFinalization, 'FeatureExtractor is not initialized with sub-components'

        log.debug 'Beginning feature extraction process...'

        // Collect basic set of application classes for discovery process
        def applicationClassCollection = classCollection.collectClasses()

        // Initialize the ClassRelevanceProvider for the application packages
        // found in ClassCollection
        def applicationClasses = applicationClassCollection.collect { it.clazz }
        def basePackagesForApplicationClasses = FnuiClassUtils.getBasePackagesFor(applicationClasses)
        classRelevanceProvider.addAllPrefixesToWhitelist(basePackagesForApplicationClasses)

        // Generate the FeatureModel
        def featureModel = featureExtraction.getFeatureModel(applicationClassCollection)

        // Perform post processing filter
        featureModel = featureModelFinalization.finalizeModel(featureModel, detailsLevel)

        log.debug 'Finished feature extraction process.'

        return featureModel
    }

    void addPrefixBlacklist(Collection<String> prefixBlacklist) {
        classRelevanceProvider.addAllPrefixesToBlacklist(prefixBlacklist)
    }

    void clearWhitelist() {
        classRelevanceProvider.clearWhitelist()
    }

    void addClassFeatureExtractor(Class classFeatureExtractor) {
        assert featureExtraction, 'FeatureExtractor.addClassFeatureExtractor called without initialized FeatureExtraction sub-component'

        featureExtraction.addExtractorClass(classFeatureExtractor)
    }
}