package fnui.feature.model

import fnui.util.constraints.Constraints
import groovy.transform.CompileStatic

/**
 * Describe a property of a class
 */
@CompileStatic
class PropertyFeature extends AbstractFeature {
    /**
     * The classFeature which contains this property feature
     */
    ClassFeature containingClass

    /**
     * The TypeFeature describing the property
     */
    TypeFeature type

    /**
     * Constraints for the property
     */
	Constraints constraints = new Constraints()

    /**
     * @return the class of the return type
     */
    Class getRawClass() {
        type.rawClass
    }

    /**
     * @return the ClassFeature of the property type if existing
     */
    ClassFeature getRawClassFeature() {
        type.feature
    }

    String toString() {
        "Property $name of $containingClass.name [type:$type, constraints:$constraints]"
    }
}
