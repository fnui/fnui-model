package fnui.util.constraints

import groovy.util.logging.Log4j

/**
 * Describes the constraints for a property / field.
 */
@Log4j
class Constraints extends LinkedHashMap<String,Object> {
    Constraints() {

    }

    Constraints(Map<String,Object> values) {
        super(values)
    }

    Boolean getBoolean(String name, Boolean defaultValue = null) {
        def value = this.get(name)

        if (value == null) {
            return defaultValue
        }

        if (value instanceof Boolean) {
            return value
        }

        if (value instanceof String) {
            if (value.equalsIgnoreCase('true')) {
                return true
            } else if (value.equalsIgnoreCase('false')){
                return false
            }
        }

        log.error "Expected 'Boolean'-value on constraint ${name} but got ${value}. Returning defaultValue '$defaultValue'"
        return defaultValue
    }
}
