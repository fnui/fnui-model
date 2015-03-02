package fnui.util

/**
 * The ClassRelevanceProvider allows to check if a certain class is relevant for
 * current usage context eg. generation of an user interface. This is needed to
 * reduce the to be inspected set of classes.
 *
 * For example classes of used libraries are often irrelevant for the UI-generation.
 */
interface ClassRelevanceProvider {

    /**
     * Check if this class is relevant by the Fn(UI) tool chain.
     *
     * @param clazz
     * @return true, if class is relevant
     */
    boolean isClassRelevant(Class clazz)

    /**
     * Check if this class name is relevant by the Fn(UI) tool chain.
     *
     * @param className
     * @return true, if className is relevant
     */
    boolean isClassNameRelevant(String className)
}