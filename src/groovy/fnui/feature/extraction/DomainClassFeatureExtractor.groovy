package fnui.feature.extraction

import fnui.feature.model.ClassFeature
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.plugins.GrailsPluginManager

/**
 * ClassFeatureExtractor for Domain classes
 */
class DomainClassFeatureExtractor extends AbstractClassFeatureExtractor {
    static final String DOMAIN_EXTRACTOR = 'Domain'
    static final List<String> BASIC_DOMAIN_PROPERTIES = ['class', 'metaClass', 'dirtyPropertyNames', 'dirty',
                                                         'attached', 'errors', 'properties']

    @Override
    String getHandles() {
        DOMAIN_EXTRACTOR
    }

    @Override
    ClassFeature getFeature(Class clazz, String artefactType) {
        def feature = newClassFeature(clazz, artefactType)

        extractBasicInfos(clazz, feature)
        extractMethodFeatures(clazz, feature)
        extractPropertyFeatures(clazz, feature, BASIC_DOMAIN_PROPERTIES)

        GrailsDomainClass domainClass = (GrailsDomainClass) grailsApplication.getDomainClass(feature.fullName)
        assert domainClass

        if (domainClass.mappingStrategy != 'GORM') {
            log.info "ClassFeature ${clazz.name} for DomainClass may miss some informations due to an unsupported mapping strategy."
        }

        extractDomainClassMetadata(feature, domainClass)

        return feature
    }

    void extractDomainClassMetadata(ClassFeature classFeature, GrailsDomainClass domainClass) {
        classFeature.metadata['domainClass'] = domainClass

        extractIdentifierInfo(classFeature, domainClass)
        extractVersionInfo(classFeature, domainClass)
        extractDomainPropertyInfos(classFeature, domainClass)
    }

    void extractDomainPropertyInfos(ClassFeature classFeature, GrailsDomainClass domainClass) {
        classFeature.propertyFeaturesCollection.each { property ->
            def domainProperty = domainClass.getPropertyByName(property.name)
            def meta = property.metadata
            meta['domainProperty'] = domainProperty
            meta['oneToOne'] = domainProperty.oneToOne
            meta['manyToOne'] = domainProperty.manyToOne
            meta['oneToMany'] = domainProperty.oneToMany
            meta['manyToMany'] = domainProperty.manyToMany
            meta['bidirectional'] = domainProperty.bidirectional
            meta['owningSide'] = domainProperty.owningSide
            meta['optional'] = domainProperty.optional
            meta['association'] = domainProperty.association
        }
    }

    void extractIdentifierInfo(ClassFeature classFeature, GrailsDomainClass domainClass) {
        def identifier = domainClass.identifier

        if (identifier) {
            def propertyFeature = classFeature.propertyFeatures[identifier.name]
            def constraints = propertyFeature.constraints

            def identity = null
            if (hasHibernate()) {
                // TODO: get information about the identifier mapping
                // eg. org.codehaus.groovy.grails.orm.hibernat.cfg.CompositeIdentity
            }

            constraints['identifier'] = constraints['identifier'] ?: identity ?: true
        }
    }

    void extractVersionInfo(ClassFeature classFeature, GrailsDomainClass domainClass) {
        def version = domainClass.version

        if (version) {
            def propertyFeature = classFeature.propertyFeatures[version.name]
            def constraints = propertyFeature.constraints

            constraints['version'] = constraints.getBoolean('version') ?: true
        }
    }

    private boolean hasHibernate() {
        pluginManager?.hasGrailsPlugin('hibernate') || pluginManager?.hasGrailsPlugin('hibernate4')
    }

    private GrailsPluginManager getPluginManager() {
        grailsApplication.mainContext.pluginManager
    }
}
