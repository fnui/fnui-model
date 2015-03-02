package fnui.feature.classcollection

/**
 * ClassCollector implementations search for classes of an application.
 */
interface ClassCollector {

    /**
     * @return list of class descriptions found by this ClassCollector
     */
	List<ClassFileDescriptor> getClasses()
}
