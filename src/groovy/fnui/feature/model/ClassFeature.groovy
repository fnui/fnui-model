package fnui.feature.model

import groovy.transform.CompileStatic
import groovy.transform.ToString

/**
 * Describes the features of a specific class
 */
@CompileStatic
@ToString(includes = ['name', 'packageName', 'artefactType'], includeNames = true)
class ClassFeature extends AbstractFeature {
    /**
     * The FeatureModel which contains this classFeature
     */
    final FeatureModel featureModel

    /**
     * The described class
     */
    final Class clazz

    /**
     * Package name of the class
     */
    String packageName

    Integer modifier

    Class               superClass
    List<Class>         interfaces   = []
    List<MethodFeature> constructors = []

    List<String>                       propertyOrder          = []
    Map<String, PropertyFeature>       propertyFeatures       = [:]
    Map<String, StaticPropertyFeature> staticPropertyFeatures = [:]

    Map<String, MethodFeature> methodFeatures       = [:]
    Map<String, MethodFeature> staticMethodFeatures = [:]

    /**
     * Grails ArtefactType with default to 'Groovy'
     */
    final String artefactType

    /**
     * FeatureExtractor used for parsing
     */
    String featureExtractor

    ClassFeature(FeatureModel featureModel, Class clazz, String type) {
        this.featureModel = featureModel
        this.clazz = clazz
        this.artefactType = type
    }

    /**
     * @return the full qualified class name
     */
    String getFullName() {
        packageName ? "$packageName.$name" : name
    }

    /**
     * @return the name of the declaring plugin or null
     */
    String getPluginName() {
        getAnnotation(org.codehaus.groovy.grails.plugins.metadata.GrailsPlugin)?.getParameter('name')
    }

    /**
     * Add a MethodFeature as constructor of this class.
     *
     * @param methodFeature
     */
    void addConstructor(MethodFeature methodFeature) {
        methodFeature.classFeature = this
        constructors << methodFeature
    }

    /**
     * Add a MethodFeature to this class.
     *
     * @param propertyFeature
     */
    void addProperty(PropertyFeature propertyFeature) {
        propertyFeature.containingClass = this
        propertyFeatures[propertyFeature.name] = propertyFeature

        if (!propertyOrder.contains(propertyFeature.name)) {
            propertyOrder << propertyFeature.name
        }
    }

    /**
     * Add a StaticPropertyFeature to this class
     *
     * @param propertyFeature
     */
    void addStaticProperty(StaticPropertyFeature propertyFeature) {
        propertyFeature.containingClass = this
        staticPropertyFeatures[propertyFeature.name] = propertyFeature
    }

    /**
     * Adds a MethodFeature to this class.
     *
     * @param methodFeature
     */
    void addMethod(MethodFeature methodFeature) {
        methodFeature.classFeature = this
        methodFeatures[methodFeature.name] = methodFeature
    }

    /**
     * Adds a StaticMethodFeature to this class.
     *
     * @param methodFeature
     */
    void addStaticMethod(MethodFeature methodFeature) {
        methodFeature.classFeature = this
        staticMethodFeatures[methodFeature.name] = methodFeature
    }

    PropertyFeature getPropertyFeature(String name) {
        propertyFeatures[name]
    }

    /**
     * @return a view to the propertyFeature mapping of the class
     */
    Collection<PropertyFeature> getPropertyFeaturesCollection() {
        propertyFeatures.values()
    }

    /**
     * @return a view to the staticPropertyFeature mapping of the class
     */
    Collection<StaticPropertyFeature> getStaticPropertyFeaturesCollection() {
        staticPropertyFeatures.values()
    }

    /**
     * @return a view to the methodFeature mapping of the class
     */
    Collection<MethodFeature> getMethodFeaturesCollection() {
        methodFeatures.values()
    }

    /**
     * @return a view to the staticMethodFeature mapping of the class
     */
    Collection<MethodFeature> getStaticMethodFeaturesCollection() {
        staticMethodFeatures.values()
    }
}
