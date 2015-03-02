package fnui.model.definition

/**
 * Contains the declarative user interface definitions extracted from @UserInterface-closure.
 *
 * The implementation remembers if definition was set in the class-closure or method-closure.
 */
class UserInterfaceDefinition {
    Map<String, Object> classDefinitions = [:]
    Map<String, Object> methodDefinitions = [:]

    /**
     * Checks if the definition contains the given key.
     *
     * @param key
     * @return
     */
    boolean containsKey(String key) {
        methodDefinitions.containsKey(key) ?: classDefinitions.get(key)
    }

    /**
     * Gets the value for the given key. Definitions in the method-closure are preferred.
     *
     * @param key
     * @return
     */
    Object get(String key) {
        methodDefinitions.get(key) ?: classDefinitions.get(key)
    }

    /**
     * Gets the value for the given key. Definitions in the method-closure are preferred.
     *
     * @param property
     * @return
     */
    Object getProperty(String property) {
        get(property)
    }

    /**
     * Gets the value for the given key. Definitions in the method-closure are preferred.
     *
     * @param key
     * @return
     */
    Object getAt(String key) {
        get(key)
    }

    /**
     * Gets the value for the given key from the method-closure.
     *
     * @param key
     * @return
     */
    Object getMethodValue(String key) {
        methodDefinitions.get(key)
    }

    /**
     * Gets the value for the given key from the class-closure.
     *
     * @param key
     * @return
     */
    Object getClassValue(String key) {
        classDefinitions.get(key)
    }

    /**
     * Put all provided key-value-paris into the method-closure mapping.
     *
     * @param map
     */
    void putAllMethodValues(Map<String, Object> map) {
        methodDefinitions.putAll(map)
    }

    /**
     * Put all provided key-value-paris into the class-closure mapping.
     *
     * @param map
     */
    void putAllClassValues(Map<String, Object> map) {
        classDefinitions.putAll(map)
    }

    String toString() {
        HashSet<String> keys = new HashSet<>()
        keys.addAll(classDefinitions.keySet())
        keys.addAll(methodDefinitions.keySet())
        "UserInterfaceDefinition(${keys})"
    }
}
