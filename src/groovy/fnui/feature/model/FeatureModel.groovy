package fnui.feature.model

import groovy.transform.CompileStatic

/**
 * The FeatureModel describes the classes of an application with help of ClassFeatures.
 */
@CompileStatic
class FeatureModel {
	private Map<Class,ClassFeature> classFeatures = [:]
	private Map<String,Map<Class,ClassFeature>> typeClassFeatures = [:]

    /**
     * Checks of this featureModel describes the provided class.
     *
     * @param clazz
     * @return true, if class describes provided class
     */
    boolean containsClass(Class<?> clazz) {
        classFeatures.containsKey(clazz)
    }

    /**
     * Get the ClassFeature for the specific class.
     *
     * @param clazz
     * @return the classFeature or null if clazz is not described in the model
     */
	ClassFeature getFeaturesOfClass(Class<?> clazz) {
        return classFeatures[clazz]
	}

    /**
     * Gets all class features for a certain artifact type.
     *
     * @param type of the artefact eg Controller or Service
     * @return
     */
	List<ClassFeature> getFeaturesOfArtefactType(String type) {
        List<ClassFeature> list = []
        list.addAll(getArtefactTypeMap(type).values())
        return list
	}

    /**
     * @return a view to all ClassFeature of this model
     */
	List<ClassFeature> getFeaturesOfAllClasses() {
        return new ArrayList<>(classFeatures.values())
	}

    /**
     * Gets the Class => ClassFeature map for artefact type.
     *
     * @param type eg. Controller or Service or Groovy (src-files)
     * @return map of Class => ClassFeature
     */
	Map<Class,ClassFeature> getArtefactTypeMap(String type) {
		def map = typeClassFeatures[type]
		
		if (map == null) {
			map = [:]
			typeClassFeatures[type] = map
		}
		
		return map
	}

    /**
     * Adds a ClassFeature to this model.
     *
     * @param classFeature
     */
	void addClass(ClassFeature classFeature) {
		classFeatures[classFeature.clazz] = classFeature
		getArtefactTypeMap(classFeature.artefactType)[classFeature.clazz] = classFeature
	}
}
