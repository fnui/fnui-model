package fnui.feature.classcollection

import grails.util.BuildSettings
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.commons.GrailsApplication

@CompileStatic
@Log4j
/**
 * Find classes defined in the ./src/[groovy|java]/-source-paths of a grails project and its source plugins
 */
class SourceClassCollector implements ClassCollector {

	BuildSettings buildSettings
	GrailsApplication grailsApplication

	SourceClassCollector(BuildSettings buildSettings, GrailsApplication grailsApplication) {
		this.buildSettings = buildSettings
		this.grailsApplication = grailsApplication
	}

	@Override
	public List<ClassFileDescriptor> getClasses() {
		Set<String> knownClasses = new HashSet<>()
        List<ClassFileDescriptor> classes = []

		for (File path:getSourcePaths()) {
            for (ClassFileDescriptor descriptor:traversePackageStructure(path, '')) {
                if (!descriptor) {
                    continue
                }

                def className = descriptor.clazz.name

                if (knownClasses.contains(className)) {
                    log.info "Class '${className}' found in '${descriptor.path}' was ignored because was overridden."
                    continue
                }

                knownClasses << className
                classes << descriptor
            }
		}

		return classes
	}

	private List<ClassFileDescriptor> traversePackageStructure(File packagePath, String packageName) {
		List<ClassFileDescriptor>  classes = []
		for (File file: packagePath.listFiles()) {
			if (file.directory) {
				classes.addAll traversePackageStructure(file, packageName ? "${packageName}.${file.name}".toString() : file.name)
			} else if (file.name ==~ /.*\.groovy/ ){
				String className = packageName ? "$packageName.${file.name[0..-8]}" : file.name[0..-8]
				classes << createClassFileDescriptor(className, 'groovy', file.absolutePath)
			} else if (file.name ==~ /.*\.java/ ){
				String className = packageName ? "$packageName.${file.name[0..-6]}" : file.name[0..-6]
				classes << createClassFileDescriptor(className, 'java', file.absolutePath)
			}
		}

		return classes
	}

	private ClassFileDescriptor createClassFileDescriptor(String className, String type, String path) {
		try {
			log.info "Found class ${className} in ${path}"
			def clazz = grailsApplication.classLoader.loadClass(className)
			return new ClassFileDescriptor(clazz:clazz, type:type, path:path)
		} catch (ClassNotFoundException e) {
			log.warn "Found file ${path} but no matching class ${className}"
			return null
		}
	}

	private List<File> getSourcePaths() {
		List<File> sourcePaths = []

		def addFilePath = {  File projectPath, String relativePath ->
			def file = new File("$projectPath/$relativePath")
			file.exists() && sourcePaths << file
		}

		def addFilePathForProject = { File projectDir ->
			addFilePath projectDir, 'src/java'
			addFilePath projectDir, 'src/groovy'
		}

		buildSettings.pluginBaseDirectories.each { addFilePathForProject it as File }
        buildSettings.inlinePluginDirectories.each { addFilePathForProject it }
		addFilePathForProject buildSettings.baseDir

		return sourcePaths
	}
}