package fnui.feature.model

import groovy.transform.CompileStatic

/**
 *
 */
@CompileStatic
class AnnotationFeature {
    Class annotationClass
    Map<String,Object> parameters = [:]

    String getName() {
        annotationClass.name
    }

    /**
     * The value of the annotation parameter with the given name.
     *
     * @param name of the parameter
     * @return the defined value
     */
    Object getParameter(String name) {
        parameters[name]
    }

    /**
     * Defines the value of the parameter.
     *
     * @param name of the parameter
     * @param value defined value
     * @return
     */
    void setParameter(String name, Object value) {
        parameters[name] = value
    }

    String toString() {
        "${annotationClass.name}(${parameters})"
    }
}
