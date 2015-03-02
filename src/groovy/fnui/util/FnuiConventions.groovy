package fnui.util

import fnui.feature.model.ClassFeature
import fnui.feature.model.TypeFeature

/**
 * Provides methods to support naming and other conventions in (FN)UI.
 */
abstract class FnuiConventions {
    static final String PLURAL_SUFFIX = 'List'

    /**
     * Gives the primary name for a artefact class. Examples:
     *  - ExampleService => Example
     *  - SomethingController => Something
     *
     * @param classFeature for an typical Grails artefact (ends with suffix name)
     * @return primary name
     */
    static String getPrimaryNameForArtefact(ClassFeature classFeature) {
        removeSuffix(classFeature.name, classFeature.artefactType)
    }

    /**
     * Gives for an Grails service artefact a matching Controller name. Examples:
     *  - OtherService => OtherController
     *
     * @param classFeature for an typical Grails service
     * @return controller name for the service
     */
    static String getControllerNameForService(ClassFeature classFeature) {
        getPrimaryNameForArtefact(classFeature) + 'Controller'
    }

    /**
     * Gives the simple name for the matching controller of an Grails service. Example:
     *  - ExampleService => example
     *  - BookLedgerService => bookLedger
     *
     * @param classFeature for an typical Grails service
     * @return simple controller name
     */
    static String getSimpleControllerNameForService(ClassFeature classFeature) {
        decapitalize(getPrimaryNameForArtefact(classFeature))
    }

    /**
     * Generate an variable name for a ClassFeature. Examples:
     *  - a.b.TestClass => testClass
     *  - Pattern => pattern
     *
     * @param classFeature
     * @return varName
     */
    static String getVarName(ClassFeature classFeature) {
        decapitalize(classFeature.name)
    }

    /**
     * Generate an variable name for a TypeFeature. Examples:
     *  - a.b.TestClass => testClass
     *  - Pattern (Suffix: 'Instance') => patternInstance
     *  - List<Pattern> => patternList
     *
     * @param typeFeature
     * @param suffix to be appended (optional)
     * @return
     */
    static String getVarName(TypeFeature typeFeature, String suffix = null) {
        def describedType = typeFeature.describedType
        def name = decapitalize describedType.rawClass.simpleName

        if (suffix) {
            name += suffix
        }

        if (typeFeature.isCollection() || typeFeature.isListViewModel()) {
            return pluralize(name)
        }

        return name
    }

    private static String pluralize(String self) {
        "${self}${PLURAL_SUFFIX}"
    }

    private static String decapitalize(String self) {
        if (!self) return self
        return "${Character.toLowerCase(self.charAt(0))}${self.substring(1)}"
    }

    private static String removeSuffix(String self, String suffix) {
        self.endsWith(suffix) ? self.substring(0, self.length() - suffix.length()) : self
    }
}
