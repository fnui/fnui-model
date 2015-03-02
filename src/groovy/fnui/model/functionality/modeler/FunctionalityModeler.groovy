package fnui.model.functionality.modeler

import fnui.UiGenerationException
import fnui.core.annotations.Context
import fnui.core.annotations.UserInterface
import fnui.feature.model.ClassFeature
import fnui.feature.model.FeatureModel
import fnui.feature.model.MethodFeature
import fnui.feature.model.TypeFeature
import fnui.model.definition.UserInterfaceDefinitionConverter
import fnui.model.functionality.FunctionalityGroup
import fnui.model.functionality.FunctionalityModel
import fnui.model.functionality.FunctionalityParameter
import fnui.model.functionality.FunctionalityParameterKind
import fnui.model.functionality.contextualize.FunctionalityContextualizer
import fnui.util.FnuiFeatureUtils
import groovy.util.logging.Log4j

/**
 * The FunctionalityModeler creates the FunctionalityModel based on
 * the provided FeatureModel.
 */
@Log4j
class FunctionalityModeler {
    static String SERVICE_TYPE = 'Service'

    FunctionalityContextualizer functionalityContextualizer

    private List<FeatureTranslator> translators = []

    /**
     * Create the FunctionalityModel based on the provided featureModel.
     *
     * Pre-requirement: functionalityContextualizer was defined.
     *
     * @param featureModel
     * @return
     */
    FunctionalityModel modelFunctionality(FeatureModel featureModel) {
        assert functionalityContextualizer, 'FunctionalityModeler was not initialized correctly'

        log.debug 'Starting FunctionalityModel creation process...'

        def functionalityModel = new FunctionalityModel()

        def uiServices = getUiServices(featureModel)
        for (ClassFeature cf : uiServices) {
            def group = getFunctionalityGroupFor(cf)
            functionalityModel.addGroup(group)
        }

        functionalityModel = functionalityContextualizer.contextualize(functionalityModel)

        log.debug 'Finished FunctionalityModel creation.'

        return functionalityModel
    }

    private List<ClassFeature> getUiServices(FeatureModel featureModel) {
        featureModel.getFeaturesOfArtefactType(SERVICE_TYPE).findAll { cf ->
            cf.hasAnnotation(UserInterface) && !cf.getAnnotation(UserInterface).getParameter('ignore')
        }
    }

    private FunctionalityGroup getFunctionalityGroupFor(ClassFeature classFeature) {
        def group = new FunctionalityGroup()
        group.name = classFeature.name
        group.serviceClassFeature = classFeature
        group.uiDefinition = UserInterfaceDefinitionConverter.getUiDefinitionFor(classFeature)

        for (MethodFeature methodFeature : classFeature.methodFeaturesCollection) {
            if (ignore(methodFeature)) {
                continue
            }

            def parameters = categorizeParameters(methodFeature)
            def uiDefinition = UserInterfaceDefinitionConverter.getUiDefinitionFor(methodFeature)

            def translator = null
            for (def t : translators) {
                if (t.accepts(methodFeature, parameters, uiDefinition)) {
                    translator = t; break
                }
            }

            if (!translator) {
                log.info "No feature translator for candidate method: ${methodFeature}"
                continue
            }

            def functionality = translator.translate(methodFeature, parameters, uiDefinition)

            if (!functionality) {
                log.info "FeatureTranslator ${translator.targetType} didn't translate method: ${methodFeature}"
                continue
            }

            if (group.getFunctionality(functionality.name)) {
                throw new UiGenerationException("Found multiple signtures for ${functionality.name} in ${group.name}. This is currently not supported by (FN)UI.")
            }

            group.addFunctionality(functionality)
        }

        return group
    }

    /**
     * Adds a translator to the list which is check one by one if it can handle a method definition
     * and the first which accepts the message is used.
     *
     * The before-relation is only considered while adding a new translator:
     * - [A,C] => add(B,[C]) => [A,B,C]
     * - [A] => add(B,[C]) => [A,B] => add(C,[A]) => [C,A,B]
     *
     * @param translator translator, which should be added
     * @param before defines a list of translators which should be after this if they exists already
     */
    void addTranslator(FeatureTranslator translator, List<String> before = []) {
        int minIndex = translators.size()

        for (def b : before) {
            for (int i = 0; i < translators.size(); i++) {
                if (translators[i].targetType == b) {
                    minIndex = Math.min(minIndex, i)
                }
            }
        }

        translators.add(minIndex, translator)
    }

    /**
     * Clears the translator list
     */
    void clearTranslatorList() {
        translators.clear()
    }

    private boolean ignore(MethodFeature methodFeature) {
        methodFeature.getAnnotation(UserInterface)?.getParameter('ignore')
    }

    /**
     * Categorize the method parameter in the three groups:
     *  - DOMAIN
     *  - COMMAND
     *  - OTHER
     *
     * @param methodFeature
     * @return list of FunctionalityParameter
     */
    static List<FunctionalityParameter> categorizeParameters(MethodFeature methodFeature) {
        List<FunctionalityParameter> categorized = []

        for (def parameterName:methodFeature.parameterOrder) {
            def param = methodFeature.parameters[parameterName]

            def fp = new FunctionalityParameter()
            fp.name = parameterName
            fp.propertyFeature = param
            fp.type = param.type

            def type = param.type
            def feature = type.feature
            if (feature) {
                if (FnuiFeatureUtils.isDomainObject(feature)) {
                    fp.parameterKind = FunctionalityParameterKind.DOMAIN
                } else if (FnuiFeatureUtils.isCommandObject(feature)) {
                    fp.parameterKind = FunctionalityParameterKind.COMMAND
                }

                fp.containedContext = getInnerContext(feature)
            }

            fp.parameterKind = fp.parameterKind ?: FunctionalityParameterKind.OTHER

            categorized << fp
        }

        return categorized
    }
    /**
     * Checks a classFeature of the named parameter for Context-annotated properties.
     *
     * @param classFeature of the parameter
     * @return the mapping propertyName => typeFeature
     */
    static Map<String,TypeFeature> getInnerContext(ClassFeature classFeature) {
        def context = [:]
        classFeature.propertyFeaturesCollection.findAll { propertyFeature ->
            propertyFeature.hasAnnotation(Context)
        }.each { propertyFeature ->
            context.put(propertyFeature.name, propertyFeature.type)
        }
        return context
    }

}