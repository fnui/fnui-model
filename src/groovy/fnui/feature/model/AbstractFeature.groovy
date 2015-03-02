package fnui.feature.model

import groovy.transform.CompileStatic

/**
 * Base class for annotatable features
 */
@CompileStatic
abstract class AbstractFeature {
    /**
     * Name of the feature
     */
	String name

    /**
     * Annotations for the feature
     */
    Map<Class,AnnotationFeature> annotations = [:]

    /**
     * Additional information about special features
     */
    Map<String, Object> metadata = [:]

    /**
     * Check if the annotation is present on this feature
     *
     * @param annotation class to be checked
     * @return true, if annotation will be found
     */
    boolean hasAnnotation(Class annotation) {
        annotations.containsKey(annotation)
    }

    /**
     * Get the annotation with the provided class of this feature.
     *
     * @param annotation class of the annotation
     * @return AnnotationFeature describing the annotation or null if not defined
     */
    AnnotationFeature getAnnotation(Class annotation) {
        annotations[annotation]
    }

    void addAnnotationFeature(AnnotationFeature annotationFeature) {
        annotations[annotationFeature.annotationClass] = annotationFeature
    }

    /**
     * @return view on the annotation mapping values
     */
    Collection<AnnotationFeature> getAnnotationCollection() {
        annotations.values()
    }
}
