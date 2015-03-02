package fnui.util.constraints

import org.codehaus.groovy.grails.commons.GrailsClassUtils

/**
 * Utility class with static methods for constraints-closure parsing.
 */
abstract class ConstraintsParser {
    static final String CONSTRAINTS_FIELD_NAME = 'constraints'

    /**
     *
     * ATTENTION: This doesn't support shared or implicit constraints
     *
     * @param clazz
     * @param includesExcludes
     * @return
     */
    static ConstraintsDefinition getConstraints(Class clazz, Map<String,List<String>> includesExcludes = [:]) {
        List<Map<String,Map<String,Object>>> stack = []
        while (clazz) {
            def constraints = GrailsClassUtils.getStaticFieldValue(clazz, CONSTRAINTS_FIELD_NAME)
            if (constraints && constraints instanceof Closure) {
                def collector = new ConstraintsCollector(constraints)
                stack.push(collector._constraints)

                collector.importedConstraints.reverseEach { Class c ->
                    stack.push(getConstraints(c, collector.includesExcludesMap[c]))
                }
            }
            clazz = clazz.superclass
        }

        def includes = includesExcludes['include']
        def excludes = includesExcludes['exclude']

        // combine all maps in reverse order
        // this ensure that the values of child class override parents
        ConstraintsDefinition constraintsDefinition = new ConstraintsDefinition()
        if (!stack.isEmpty()) {
            while (!stack.empty) {
                Map<String,Map<String,Object>> currentMap = stack.pop()
                for (def entry:currentMap) {
                    def propertyName = entry.key
                    def propertyConstraints = entry.value

                    if (includes) {
                        if (!isInRegexList(includes, propertyName)) {
                            continue
                        }
                    }

                    if (excludes) {
                        if (isInRegexList(excludes, propertyName)) {
                            continue
                        }
                    }

                    Map constraints = constraintsDefinition.get(propertyName)
                    if (constraints) {
                        constraints.putAll(propertyConstraints)
                    } else {
                        constraintsDefinition.put(propertyName, new Constraints(propertyConstraints))
                    }
                }
            }
        }
        return constraintsDefinition
    }

    private static boolean isInRegexList(List<String> regexList, String stringToMatch) {
        for (String regex:regexList) {
            if (stringToMatch.matches(regex)) {
                return true
            }
        }

        return false
    }

    private static class ConstraintsCollector {
        List<Class> importedConstraints = []
        Map<Class,Map<String,List<String>>> includesExcludesMap = [:]
        Map<String,Map<String,Object>> _constraints = [:]
        Closure _closure

        ConstraintsCollector(Closure closure) {
            this._closure = closure
            _closure.delegate = this
            _closure.resolveStrategy = Closure.DELEGATE_FIRST
            _closure()
        }

        void importFrom(Class clazz, Map<String,List<String>> includesExcludes = [:]) {
            importedConstraints << clazz
            includesExcludesMap[clazz] = includesExcludes
        }

        def invokeMethod(String name, args) {
            // Format of the constraints closure is like:
            // static constraints = {
            //     propertyA(constraint1:value1,constraint2:value2)
            //     propertyB(constraint2:value1)
            // }
            //
            // If executed it will call invokeMethod with name 'propertyA' and arguments [[contraint1:value1,constraint2:value2]] etc.
            _constraints.put(name, args[0])
        }
    }
}
