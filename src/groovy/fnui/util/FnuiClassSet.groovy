package fnui.util

import groovy.transform.CompileStatic

/**
 * Special set implementation based on HashSet which silently ignores adds for
 * certain classes based on the defined ClassReferenceProvider.
 */
@CompileStatic
class FnuiClassSet extends HashSet<Class> {

    ClassRelevanceProvider classRelevanceProvider

    /**
     * @param provider to check if class is addable to set
     */
    FnuiClassSet(ClassRelevanceProvider provider = null) {
        classRelevanceProvider = provider
    }

    @Override
    boolean add(Class clazz) {
        if (!clazz) {
            return false
        }

        if (classRelevanceProvider && !classRelevanceProvider.isClassRelevant(clazz)) {
            return false
        }

        return super.add(clazz)
    }
}
