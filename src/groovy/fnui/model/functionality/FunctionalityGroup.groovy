package fnui.model.functionality

import fnui.feature.model.ClassFeature
import fnui.feature.model.TypeFeature
import fnui.model.definition.UserInterfaceDefinition
import fnui.util.FnuiConventions
import groovy.transform.Canonical
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType

/**
 * A FunctionalityGroup depicts the set of Functionalities of a service.
 */
@Canonical
class FunctionalityGroup {
    String name

    ClassFeature serviceClassFeature

    UserInterfaceDefinition uiDefinition

    FunctionalityModel model

    Map<String,Functionality> functionalities = [:]

    /**
     * Find the functionality with the given name in this group.
     *
     * @param name
     * @return the functionality or null if no functionality with the given name exists in the group
     */
    Functionality getFunctionality(String name) {
        functionalities[name]
    }

    /**
     * Adds a functionality to the group
     *
     * @param functionality
     */
    void addFunctionality(Functionality functionality) {
        assert functionality

        functionalities[functionality.name] = functionality
        functionality.group = this
    }

    /**
     * @return a view of the functionality collection
     */
    Collection<Functionality> getFunctionalityCollection() {
        functionalities.values()
    }

    /**
     * @return packageName of the underlying service class
     */
    String getPackageName() {
        serviceClassFeature.packageName
    }

    /**
     * @return the TypeFeature which describes the service class variable
     */
    TypeFeature getServiceType() {
        new TypeFeature(serviceClassFeature)
    }

    /**
     * @return the conventional varName for the service which is used for dependency injection
     */
    String getServiceVarName() {
        FnuiConventions.getVarName(serviceClassFeature)
    }

    /**
     * Helper for iteration all functionalities of the group
     *
     * @param closure which is called with each Functionality of the group
     */
    void eachFunctionality(@ClosureParams(value=SimpleType.class, options="fnui.model.functionality.Functionality") Closure closure) {
        for (def fn:functionalityCollection) {
            closure.call(fn)
        }
    }
}
