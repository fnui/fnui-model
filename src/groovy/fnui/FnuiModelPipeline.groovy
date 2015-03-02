package fnui

import fnui.feature.FeatureExtractor
import fnui.feature.extraction.ControllerClassFeatureExtractor
import fnui.feature.extraction.DomainClassFeatureExtractor
import fnui.feature.extraction.ServiceClassFeatureExtractor
import fnui.feature.extraction.TagLibClassFeatureExtractor
import fnui.model.AbstractUserInterfaceModel
import fnui.model.UiModelGenerator
import fnui.model.aui.controller.ServiceCallHandler
import fnui.model.aui.property.*
import fnui.model.aui.taskmodel.TaskFlowHandler
import fnui.model.aui.taskmodel.TaskFlowsHandler
import fnui.model.aui.taskmodel.TaskGroupHandler
import fnui.model.aui.taskmodel.flow.*
import fnui.model.aui.view.ContextBoxHandler
import fnui.model.aui.view.ContextOperationsHandler
import fnui.model.aui.view.OperationsBarHandler
import fnui.model.aui.view.ViewHandler
import fnui.model.functionality.contextualize.FunctionalityContextualizer
import fnui.model.functionality.contextualize.linkers.*
import fnui.model.functionality.modeler.FunctionalityModeler
import fnui.model.functionality.modeler.translators.create.CommandCreateFeatureTranslator
import fnui.model.functionality.modeler.translators.create.DomainCreateFeatureTranslator
import fnui.model.functionality.modeler.translators.list.PaginatedListFeatureTranslator
import fnui.model.functionality.modeler.translators.list.SimpleListFeatureTranslator
import fnui.model.functionality.modeler.translators.operation.InputDomainOperationFeatureTranslator
import fnui.model.functionality.modeler.translators.operation.SimpleDomainOperationFeatureTranslator
import fnui.model.functionality.modeler.translators.show.DomainShowFeatureTranslator
import fnui.model.functionality.modeler.translators.show.SimpleShowFeatureTranslator
import fnui.model.functionality.modeler.translators.update.DomainUpdateFeatureTranslator
import fnui.model.task.modeler.TaskModeler
import fnui.model.task.modeler.translators.create.CreateFunctionalityTranslator
import fnui.model.task.modeler.translators.list.ListFunctionalityTranslator
import fnui.model.task.modeler.translators.operator.InputDomainOperationFunctionalityTranslator
import fnui.model.task.modeler.translators.operator.SimpleDomainFunctionalityTranslator
import fnui.model.task.modeler.translators.show.ShowFunctionalityTranslator
import fnui.model.task.modeler.translators.update.UpdateFunctionalityTranslator
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.GrailsPluginManager
import org.codehaus.groovy.grails.plugins.support.aware.GrailsApplicationAware

/**
 * The FnuiModelPipeline provides an convenient setup for the different tools of the
 * (FN)UI tools.
 *
 * Before using {@link FnuiModelPipeline#generateModel()} for obtaining an UI-model,
 * the pipeline has to be initialized by setting an GrailsApplication
 */
@Log4j
class FnuiModelPipeline implements GrailsApplicationAware {
    final static String CONFIGURE_CLOSURE_NAME = 'configureFnuiModelPipeline'


    GrailsApplication grailsApplication

    FeatureExtractor featureExtractor
    FunctionalityModeler functionalityModeler
    TaskModeler taskModeler
    UiModelGenerator uiModelGenerator

    /**
     * Checks if this pipeline is initialized.
     *
     * @return true if initialized
     */
    boolean isExecutable() {
        featureExtractor && functionalityModeler && taskModeler && uiModelGenerator
    }

    /**
     * Generates an UI-model for the current grailsApplication.
     *
     * @return an AbstractUserInterfaceModel
     */
    AbstractUserInterfaceModel generateModel() {
        assert featureExtractor
        assert functionalityModeler
        assert taskModeler
        assert uiModelGenerator

        def featureModel = featureExtractor.extract()
        return uiModelGenerator.generateModel(featureModel)
    }

    /**
     * Initialize this pipeline with instances of all needed pipeline components.
     * The pipeline components are also initialized.
     * Does not change already set subcomponents.
     *
     * Pre-Requirement: grailsApplication has to be set.
     */
    void initializePipeline() {
        assert grailsApplication

        featureExtractor = featureExtractor ?: newFeatureExtractor()
        functionalityModeler = functionalityModeler ?: newFunctionalityModeler()
        taskModeler = taskModeler ?: newTaskModeler()
        uiModelGenerator = uiModelGenerator ?: newModelGenerator()
        uiModelGenerator.functionalityModeler = uiModelGenerator.functionalityModeler ?: functionalityModeler
        uiModelGenerator.taskModeler = uiModelGenerator.taskModeler ?: taskModeler

        configurePipeline()
    }

    void configurePipeline() {

        assert grailsApplication

        GrailsPluginManager pluginManager = grailsApplication.mainContext.pluginManager

        pluginManager.allPlugins.each { plugin ->
            if (plugin.supportsCurrentScopeAndEnvironment()) {
                try {
                    def instance = plugin.instance
                    if (!instance.hasProperty(CONFIGURE_CLOSURE_NAME)) {
                        return
                    }

                    Closure c = (Closure) instance.getProperty(CONFIGURE_CLOSURE_NAME)
                    c.setDelegate(this)
                    c.call(this)
                } catch (Throwable t) {
                    log.error "Error configuring dynamic methods for plugin ${plugin}: ${t.message}", t
                }
            }
        }
    }

    private FeatureExtractor newFeatureExtractor() {
        def extractor = new FeatureExtractor()
        extractor.grailsApplication = grailsApplication
        extractor.initializeExtractor(true)

        def defaultBlacklist = ['org.codehaus.groovy.grails.plugins.codecs',
                                'org.codehaus.groovy.grails.plugins.web',
                                'grails.plugin.cache',
                                'asset.pipeline',
                                'grails.plugin.databasemigration',
                                'fnui.model',
                                'fnui.feature',
                                'fnui.generator',
                                'fnui.core.utils',
                                'fnui.core.feature',
                                'fnui.core.model']
        extractor.addPrefixBlacklist(defaultBlacklist)

        extractor.addClassFeatureExtractor(DomainClassFeatureExtractor)
        extractor.addClassFeatureExtractor(ControllerClassFeatureExtractor)
        extractor.addClassFeatureExtractor(ServiceClassFeatureExtractor)
        extractor.addClassFeatureExtractor(TagLibClassFeatureExtractor)

        return extractor
    }

    private FunctionalityModeler newFunctionalityModeler() {
        def functionalityModeler = new FunctionalityModeler()
        functionalityModeler.addTranslator(new SimpleListFeatureTranslator())
        functionalityModeler.addTranslator(new PaginatedListFeatureTranslator())

        functionalityModeler.addTranslator(new SimpleShowFeatureTranslator())
        functionalityModeler.addTranslator(new DomainShowFeatureTranslator())

        functionalityModeler.addTranslator(new DomainCreateFeatureTranslator())
        functionalityModeler.addTranslator(new CommandCreateFeatureTranslator())

        functionalityModeler.addTranslator(new DomainUpdateFeatureTranslator())

        functionalityModeler.addTranslator(new SimpleDomainOperationFeatureTranslator())
        functionalityModeler.addTranslator(new InputDomainOperationFeatureTranslator())

        def fc = new FunctionalityContextualizer()
        fc.addLinker(new ShowFunctionalityLinker())
        fc.addLinker(new CreateFunctionalityLinker())
        fc.addLinker(new UpdateFunctionalityLinker())
        fc.addLinker(new OperationFunctionalityLinker())
        fc.addLinker(new ListFunctionalityLinker())
        functionalityModeler.functionalityContextualizer = fc

        return functionalityModeler
    }

    private TaskModeler newTaskModeler() {
        def taskModeler = new TaskModeler()
        taskModeler.addTranslator(new ListFunctionalityTranslator())
        taskModeler.addTranslator(new ShowFunctionalityTranslator())
        taskModeler.addTranslator(new CreateFunctionalityTranslator())
        taskModeler.addTranslator(new UpdateFunctionalityTranslator())


        taskModeler.addTranslator(new SimpleDomainFunctionalityTranslator())
        taskModeler.addTranslator(new InputDomainOperationFunctionalityTranslator())

        return taskModeler
    }

    private UiModelGenerator newModelGenerator() {
        def auiGenerator = new UiModelGenerator()
        auiGenerator.functionalityModeler = newFunctionalityModeler()
        auiGenerator.taskModeler = newTaskModeler()

        // task model handler
        auiGenerator.registerHandler(new TaskGroupHandler())
        auiGenerator.registerHandler(new TaskFlowsHandler())
        auiGenerator.registerHandler(new TaskFlowHandler())

        // task flow handler
        auiGenerator.registerHandler(new PaginatedListTaskFlowHandler())
        auiGenerator.registerHandler(new SimpleListTaskFlowHandler())
        auiGenerator.registerHandler(new ShowTaskFlowHandler())
        auiGenerator.registerHandler(new CreateTaskFlowHandler())
        auiGenerator.registerHandler(new UpdateTaskFlowHandler())
        auiGenerator.registerHandler(new SimpleOperationTaskFlowHandler())
        auiGenerator.registerHandler(new InputOperationTaskFlowHandler())

        // controller handler
        auiGenerator.registerHandler(new ServiceCallHandler())

        // view handler
        auiGenerator.registerHandler(new ViewHandler())
        auiGenerator.registerHandler(new ContextBoxHandler())
        auiGenerator.registerHandler(new OperationsBarHandler())
        auiGenerator.registerHandler(new ContextOperationsHandler())

        // property handler
        auiGenerator.registerHandler(new PropertyHandler())
        auiGenerator.registerHandler(new ListPropertiesHandler())
        auiGenerator.registerHandler(new DetailsPropertiesHandler())
        auiGenerator.registerHandler(new FormEntriesHandler())
        auiGenerator.registerHandler(new FormEntryHandler())

        return auiGenerator
    }
}
