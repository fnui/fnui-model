package fnui.feature.classcollection

import groovy.transform.CompileStatic
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.commons.ArtefactHandler
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsClass

/**
 * Collects Grails artefact classes of an application.
 */
@CompileStatic
@Log4j
class GrailsClassCollector implements ClassCollector {
	
	GrailsApplication grailsApplication
	
	GrailsClassCollector(GrailsApplication grailsApplication) {
		this.grailsApplication = grailsApplication
	}

    @Override
	public List<ClassFileDescriptor> getClasses() {
		List<ClassFileDescriptor> classes = []
		
		for (ArtefactHandler handler:grailsApplication.artefactHandlers) {
			String typeName = handler.type
			for (GrailsClass grailsClass:grailsApplication.getArtefacts(typeName)) {
				Class clazz = grailsClass.clazz
                if (clazz) {
                    classes << createClassFileDescriptor(clazz, typeName)
                }
			}
		}
		
		return classes
	}
	
	private ClassFileDescriptor createClassFileDescriptor(Class clazz, String type) {
		log.trace "Found class ${clazz}"
		// TODO [RECONSIDER] ClassFileDescriptor would like to get a path to the source file but currently its not used and therefore we don't bother to implement it
		// implementation idea: get the set of paths for main app and source plugins for each artefact type and search for matching data
		// grailsApplication.mainContext.pluginManager.allPlugins
		return new ClassFileDescriptor(clazz:clazz, type:type)
	}
}