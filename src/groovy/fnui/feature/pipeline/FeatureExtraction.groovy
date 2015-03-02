package fnui.feature.pipeline

import fnui.feature.classcollection.ClassFileDescriptor
import fnui.feature.extraction.ClassFeatureExtractor
import fnui.feature.extraction.GroovyClassFeatureExtractor
import fnui.feature.model.FeatureModel
import fnui.util.ClassRelevanceProvider
import fnui.util.FnuiFeatureUtils
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.commons.GrailsApplication

/**
 * The FeatureExtraction phase generated the feature model on the base of a collection of ClassFileDescription.
 */
@Log4j
class FeatureExtraction {
    private List<Class> extractorClasses = [GroovyClassFeatureExtractor]

    GrailsApplication grailsApplication
    ClassRelevanceProvider classRelevanceProvider

    FeatureExtraction(GrailsApplication grailsApplication, ClassRelevanceProvider classRelevanceProvider) {
        this.grailsApplication = grailsApplication
        this.classRelevanceProvider = classRelevanceProvider
    }

    /**
     * Generate a feature model based of the provided class files and their references.
     *
     * @param classFileDescriptors
     * @return featureModel
     */
    FeatureModel getFeatureModel(Collection<ClassFileDescriptor> classFileDescriptors) {
        Queue<ClassFileDescriptor> extractionQueue = [] as Queue
        extractionQueue.addAll(classFileDescriptors)

        def featureModel = new FeatureModel()
        def extractors = getClassFeatureExtractors(featureModel)

        ClassFileDescriptor descriptor
		while (descriptor = extractionQueue.poll()) {
            def clazz = descriptor.clazz
            def type = descriptor.type
            def extractor = extractors[type] ?: extractors[defaultExtractor]

            boolean classShouldBeExtracted = !featureModel.containsClass(clazz) && classRelevanceProvider.isClassRelevant(clazz)
            if (!classShouldBeExtracted) {
                continue
            }
            def classFeature = extractor.getFeature(clazz, type)
            featureModel.addClass(classFeature)

            FnuiFeatureUtils.getReferencedClasses(classFeature, classRelevanceProvider).findAll { !featureModel.containsClass(it) && classRelevanceProvider.isClassRelevant(it) }.each {
                // ASSUMPTION: All special classes (Grails artefacts) are already in queue because they are found in
                // the class collection process. Classes added here can therefore be parsed by default extractor.
                extractionQueue << new ClassFileDescriptor(it, defaultExtractor, null)
            }
        }

		return featureModel
	}

    /**
     * Adds an class of type ClassFeatureExtractor to the extraction process
     *
     * @param extractor Class of which implements ClassFeatureExtractor
     */
    void addExtractorClass(Class extractor) {
        assert ClassFeatureExtractor.isAssignableFrom(extractor)

        extractorClasses << extractor
    }

    private String getDefaultExtractor() {
        GroovyClassFeatureExtractor.GROOVY_FEATURE_EXTRACTOR
    }

    private Map<String, ClassFeatureExtractor> getClassFeatureExtractors(FeatureModel featureModel) {
        def extractors = [:]

        for (def c:extractorClasses) {
            ClassFeatureExtractor extractor = c.newInstance()
            extractor.featureModel = featureModel
            extractor.grailsApplication = grailsApplication
            extractors[extractor.handles] = extractor
        }

        return extractors
    }
}