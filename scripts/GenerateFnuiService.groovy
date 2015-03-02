import grails.util.GrailsNameUtils

includeTargets << grailsScript("_GrailsBootstrap")
includeTargets << grailsScript("_GrailsCreateArtifacts")

target(generateFnuiService: "Generates a service providing CRUD functionality for a domain class following the (FN)UI model") {
    depends(checkVersion, parseArguments, packageApp, loadApp)

    promptForName(type: "Domain Class")

    String name = argsMap['params'][0]
    name = name.indexOf('.') > 0 ? name : GrailsNameUtils.getClassNameRepresentation(name)
    def domainClass = grailsApp.getDomainClass(name)

    if (!domainClass) {
        event("StatusFinal", ["No domain class found for name ${name}. Please try again and enter a valid domain class name"])
        return
    }

    def fnuiServiceTemplategenerator = classLoader.loadClass('fnui.util.FnuiServiceTemplateGenerator').newInstance()
    fnuiServiceTemplategenerator.grailsApplication = grailsApp
    fnuiServiceTemplategenerator.pluginManager = pluginManager

    event("StatusUpdate", ["Generating service for domain class ${domainClass.fullName}"])
    fnuiServiceTemplategenerator.generateCrudService(domainClass)
    event("GenerateServiceEnd", [domainClass.fullName])
    event("StatusFinal", ["Finished generation for domain class ${domainClass.fullName}"])
}

USAGE = """
    generate-fnui-service [NAME]
where
    NAME       = a domain class name (case-sensitive)
"""

setDefaultTarget(generateFnuiService)
