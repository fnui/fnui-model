package fnui.feature.model
import groovy.transform.CompileStatic
import groovy.transform.ToString

/**
 * Describes a static property of an class and its default value.
 */
@CompileStatic
@ToString(includes = ['name', 'type', 'constraints', 'defaultValue'], includeNames = true)
class StaticPropertyFeature extends PropertyFeature {
    /**
     * The value of this static property if determinable.
     */
    Object defaultValue
}
