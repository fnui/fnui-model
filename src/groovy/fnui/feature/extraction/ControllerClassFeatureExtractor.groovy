package fnui.feature.extraction

import fnui.feature.model.ClassFeature

/**
 * ClassFeatureExtractor for Controller classes
 */
class ControllerClassFeatureExtractor extends AbstractClassFeatureExtractor {
    static final String CONTROLLER_EXTRACTOR = 'Controller'
    static final List<String> BASIC_DOMAIN_PROPERTIES = ['class', 'metaClass', 'actionName', 'actionUri', 'applicationContext',
                                                         'chainModel', 'controllerClass', 'controllerName', 'controllerNamespace',
                                                         'controllerUri', 'errors', 'flash', 'grailsApplication', 'grailsAttributes',
                                                         'instanceControllerTagLibraryApi', 'instanceControllersApi',
                                                         'instanceControllersRestApi', 'modelAndView', 'params', 'pluginContextPath',
                                                         'request', 'response', 'servletContext', 'session', 'webRequest']

    @Override
    String getHandles() {
        CONTROLLER_EXTRACTOR
    }

    @Override
    ClassFeature getFeature(Class clazz, String artefactType) {
        def feature = newClassFeature(clazz, artefactType)

        extractBasicInfos(clazz, feature)
        extractMethodFeatures(clazz, feature)
        extractPropertyFeatures(clazz, feature, BASIC_DOMAIN_PROPERTIES)
        // TODO: extract and persist persistance layer infos

        return feature
    }
}
