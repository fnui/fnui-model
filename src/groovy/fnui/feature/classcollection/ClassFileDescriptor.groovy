package fnui.feature.classcollection

import groovy.transform.Canonical

/**
 * Describes the artefact type and path for a class.
 */
@Canonical
class ClassFileDescriptor {
    /**
     * Described class
     */
	Class clazz

    /**
     * artefact type of the class with 'Groovy' as default.
     */
	String type

    /**
     * path to file descriptor (optional)
     */
	String path
}
