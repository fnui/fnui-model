package fnui.feature.pipeline

import fnui.core.annotations.UserInterface
import fnui.feature.FeatureModelDetailLevel
import fnui.feature.model.ClassFeature
import fnui.feature.model.FeatureModel
import fnui.util.FnuiFeatureUtils

import static fnui.feature.FeatureModelDetailLevel.CONNECTED_COMPONENT_GENERATE_UI

/**
 * FeatureModelFinalization phase performs post-processings for the specific usage scenario of the FeatureModel.
 *
 * Currently there are two scenarios like defined in FeatureModelDetailLevel:
 *  - CONNECTED_COMPONENT_GENERATE_UI: filter ClassFeatures which are not connected with services with @UserInterface
 *  - FULL_MODEL: no post-processing eg for inspection of the model
 *
 */
class FeatureModelFinalization {

    /**
     * Finalize model for the provided usage scenario.
     *
     * @param featureModel extracted by FeatureExtraction
     * @param detailsLevel describing the usage scenario
     * @return filtered feature model
     */
	FeatureModel finalizeModel(FeatureModel featureModel, FeatureModelDetailLevel detailsLevel) {
        switch (detailsLevel) {
            case CONNECTED_COMPONENT_GENERATE_UI:
                return filterConnectedComponentGenerateUiAnnotatedClasses(featureModel)
            default:
                return featureModel
        }
	}

    private FeatureModel filterConnectedComponentGenerateUiAnnotatedClasses(FeatureModel featureModel) {
        def filteredModel = new FeatureModel()

        Stack<ClassFeature> classStack = [] as Stack
        classStack.addAll(getUserInterfaceAnnotatedClasses(featureModel))
        def referenceSets = generateReferenceSets(featureModel)

        while (classStack) {
            def classFeature = classStack.pop()
            filteredModel.addClass(classFeature)

            referenceSets[classFeature.clazz].each { ref ->
                if (filteredModel.containsClass(ref)) {
                    return
                }

                featureModel.getFeaturesOfClass(ref).each {
                    if (it) {
                        classStack.push(it)
                    }
                }
            }
        }

        return filteredModel
    }

    private Stack<ClassFeature> getUserInterfaceAnnotatedClasses(FeatureModel featureModel) {
        featureModel.featuresOfAllClasses.findAll { cf ->
            cf.hasAnnotation(UserInterface)
        } as Stack
    }

    private Map<Class,Set<Class>> generateReferenceSets(FeatureModel featureModel) {
        def map = [:]

        featureModel.featuresOfAllClasses.each { classFeature ->
            map[classFeature.clazz] = FnuiFeatureUtils.getReferencedClasses(classFeature)
        }

        map.each { clazz, classSet ->
            classSet.each { ref ->
                if (map[ref]) {
                    map[ref] << clazz
                }
            }
        }

        return map
    }
}
