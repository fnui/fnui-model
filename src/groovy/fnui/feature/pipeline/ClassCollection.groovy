package fnui.feature.pipeline

import fnui.feature.classcollection.ClassCollector
import fnui.feature.classcollection.ClassFileDescriptor
import fnui.feature.classcollection.GrailsClassCollector
import fnui.feature.classcollection.SourceClassCollector
import grails.util.BuildSettingsHolder
import org.codehaus.groovy.grails.commons.GrailsApplication

/**
 * ClassCollection phase tries to get the classes of the grails project. Not all classes will be found, but the set is a
 * good basis to work with. Everything relevant but not found yet should be found indirectly in the FeatureExtraction
 */
class ClassCollection {
	List<ClassCollector> collectors = []
	
	ClassCollection(GrailsApplication grailsApplication, boolean withoutSource = false) {
		assert grailsApplication, 'Phase ClassCollection needs a reference to grailsApplication'

        collectors << new GrailsClassCollector(grailsApplication)

		if (!withoutSource) {
			def buildSettings = BuildSettingsHolder.settings
			assert buildSettings, 'Phase ClassCollection could not obtain the BuildSettings. Unexpected call in deployment?'

			collectors << new SourceClassCollector(buildSettings, grailsApplication)
		}
	}

    /**
     * Collects all classes collectable by the ClassCollectors set for the ClassCollection phase.
     *
     * @return combined result of all underling ClassCollector instances.
     */
	List<ClassFileDescriptor> collectClasses() {
		List<ClassFileDescriptor> classes = []
		
		for (ClassCollector collector:collectors) {
            def collectionResult = collector.classes
			classes.addAll(collectionResult)
		}
		
		return classes
	}
}
