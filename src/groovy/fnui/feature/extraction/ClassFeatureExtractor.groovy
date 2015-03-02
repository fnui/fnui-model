package fnui.feature.extraction

import fnui.feature.model.ClassFeature
import fnui.feature.model.FeatureModel
import fnui.util.ClassRelevanceAware
import org.codehaus.groovy.grails.commons.GrailsApplication

/**
 * A ClassFeatureExtractor for extraction of feature of a class with the handlable artefact type.
 */
interface ClassFeatureExtractor extends ClassRelevanceAware {
    /**
     * @return the handlable artefact type
     */
    String getHandles()

    /**
     * Get the ClassFeature for the defined class.
     *
     * @param clazz
     * @param artefactType
     * @return
     */
	ClassFeature getFeature(Class clazz, String artefactType)

    /**
     * Sets the feature model for the extraction process of this instance.
     *
     * @param featureModel
     */
    void setFeatureModel(FeatureModel featureModel)

    void setGrailsApplication(GrailsApplication grailsApplication)
}
