package fnui.util

/**
 * The DefaultClassRelevanceProvider maintains black- and whitelists for class packages
 * to check for relevance for the FeatureExtraction process.
 *
 * This has two purposes:
 *  - not building a graph for all the classes of classpath
 *  - ignoring classes which are not processable by the FeatureExtraction (eg. strange bytecode)
 */
class DefaultClassRelevanceProvider implements ClassRelevanceProvider {

    Boolean ignoreDefaultPackage = false
    List<String> blacklist = []
    List<String> whitelist = []

    /**
     * Check if this class should be ignored by the Fn(UI) tool chain.
     *
     * First the blacklist than the whitelist is checked. If the blacklist contains a prefix or the whitelist
     * does not contains a prefix of the full class name, the class will be ignored.
     *
     * If the whitelist is empty, all classes are ignored.
     *
     * @param clazz
     * @return true , if class should be ignored
     */
    boolean isClassRelevant(Class clazz) {
        isClassNameRelevant(clazz.name)
    }

    /**
     * Check if this class name should be ignored by the Fn(UI) tool chain.
     *
     * First the blacklist than the whitelist is checked. If the blacklist contains a prefix or the whitelist
     * does not contains a prefix of the full class name, the class will be ignored.
     *
     * If the whitelist is empty, all classes are ignored.
     *
     * @param className
     * @return true , if className should be ignored
     */
    boolean isClassNameRelevant(String className) {
        boolean isInDefaultPackage = !className.contains('.')
        if (ignoreDefaultPackage && isInDefaultPackage) {
            return false
        }

        boolean blacklisted = blacklist.any { String p -> className.startsWith(p) }
        if (blacklisted) {
            return false
        }

        boolean whitelisted = whitelist.any { String p -> className.startsWith(p) }
        return whitelisted
    }

    /**
     * Adds multiple prefixes to the blacklist. Example:
     *  ['packageName','sub.packageName']
     *
     * @param classPrefixes
     */
    void addAllPrefixesToBlacklist(Collection<String> classPrefixes) {
        blacklist.addAll(classPrefixes)
    }

    /**
     * Adds multiple prefixes to the whitelist. Example:
     *  ['packageName','sub.packageName']
     *
     * @param classPrefixes
     */
    void addAllPrefixesToWhitelist(Collection<String> classPrefixes) {
        whitelist.addAll(classPrefixes)
    }

    /**
     * Clears the whitelist for this provider.
     */
    void clearWhitelist() {
        whitelist.clear()
    }
}