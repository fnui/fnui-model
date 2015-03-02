package fnui.model.definition

import fnui.UiGenerationException
import fnui.core.annotations.UserInterface
import fnui.feature.model.ClassFeature
import fnui.feature.model.MethodFeature

/**
 * The UserInterfaceDefinitionConverter translates the closure-value of a UserInterface annotation
 * into an UserInterfaceDefinition
 */
abstract class UserInterfaceDefinitionConverter {

    /**
     * Extract the UserInterfaceDefinition for the provided methodFeature.
     * The result contains also the definitions from the related classFeature.
     *
     * @param methodFeature
     * @return
     */
    final static UserInterfaceDefinition getUiDefinitionFor(MethodFeature methodFeature) {
        def uiDef = getUiDefinitionFor(methodFeature.classFeature)

        def collector = new UiDefinitionsCollector()
        def userInterfaceAnnoationClosure = methodFeature.getAnnotation(UserInterface)?.getParameter('value')

        try {
            if (userInterfaceAnnoationClosure) {
                collect(collector, userInterfaceAnnoationClosure)
            }
        } catch (e) {
            throw new UiGenerationException("Could not parse @UserInterface-closure for ${methodFeature}", e)
        }

        uiDef.putAllMethodValues(collector._props)

        return uiDef
    }

    /**
     * Extracts the UserInterfaceDefinition fr the provided classFeature.
     *
     * @param classFeature
     * @return
     */
    final static UserInterfaceDefinition getUiDefinitionFor(ClassFeature classFeature) {
        def uiDef = new UserInterfaceDefinition()

        def collector = new UiDefinitionsCollector()
        def userInterfaceAnnotationClosure = classFeature.getAnnotation(UserInterface)?.getParameter('value')

        try {
            if (userInterfaceAnnotationClosure) {
                collect(collector, userInterfaceAnnotationClosure)
            }
        } catch (e) {
            throw new UiGenerationException("Could not parse @UserInterface-closure for ${classFeature}", e)
        }

        uiDef.putAllClassValues(collector._props)

        return uiDef
    }

    private static UiDefinitionsCollector collect(UiDefinitionsCollector collector, Class closureClass) {
        Closure closure = closureClass.newInstance([collector, collector] as Object[])
        closure.call()
        collector
    }


    private static class UiDefinitionsCollector {
        Map<String, Map<String, Object>> _props = [:]

        @Override
        void setProperty(String property, Object newValue) {
            _props[property] = newValue
        }
    }
}
