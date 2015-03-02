package fnui.util

import grails.build.logging.GrailsConsole
import groovy.text.SimpleTemplateEngine
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.plugins.GrailsPluginInfo
import org.codehaus.groovy.grails.plugins.GrailsPluginManager
import org.codehaus.groovy.grails.plugins.GrailsPluginUtils
import org.codehaus.groovy.runtime.IOGroovyMethods
import org.codehaus.groovy.runtime.StringGroovyMethods
import org.springframework.core.io.AbstractResource
import org.springframework.core.io.FileSystemResource
import org.springframework.util.StringUtils

/**
 * FnuiServiceTemplateGenerator
 *
 * Heavily inspired by org.codehaus.groovy.grails.scaffolding.AbstractGrailsTemplateGenerator
 * Code was duplicated to avoid the scaffolding plugin dependency.
 */
class FnuiServiceTemplateGenerator {
    protected String basedir = '.'
    protected boolean overwrite = false
    protected SimpleTemplateEngine engine = new SimpleTemplateEngine()
    protected GrailsPluginManager pluginManager
    protected GrailsApplication grailsApplication

    void generateCrudService(GrailsDomainClass domainClass, String destDir = basedir) {
        assert domainClass && destDir

        String packagePath = domainClass.packageName.replace('.', '/')
        if (packagePath) {
            packagePath += '/'
        }

        File destFile = new File(destDir, "grails-app/services/${packagePath}${domainClass.shortName}Service.groovy")

        if (canWrite(destFile)) {
            destFile.getParentFile().mkdirs()

            BufferedWriter writer = null
            try {
                writer = new BufferedWriter(new FileWriter(destFile))
                generateCrudService(domainClass, writer)
                try {
                    writer.flush()
                } catch (IOException ignored) {
                }
            }
            finally {
                IOGroovyMethods.closeQuietly(writer)
            }
        }
    }

    protected void generateCrudService(GrailsDomainClass domainClass, Writer out) throws IOException {
        String templateText = getTemplateText('CrudService.groovy')

        def binding = ['domainClass':domainClass,
                       'className':domainClass.shortName,
                       'packageName':domainClass.packageName,
                       'propertyName':domainClass.propertyName]
        generate(templateText, binding, out)
    }

    protected void generate(String templateText, Map<String, Object> binding, Writer out) {
        try {
            engine.createTemplate(templateText).make(binding).writeTo(out)
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e)
        }
        catch (IOException e) {
            throw new RuntimeException(e)
        }
    }

    protected String getTemplateText(String template) throws IOException {
        InputStream inputStream = null
        AbstractResource templateFile = getTemplateResource(template)
        if (templateFile.exists()) {
            inputStream = templateFile.getInputStream()
        }

        return inputStream == null ? null : IOGroovyMethods.getText(inputStream)
    }

    protected AbstractResource getTemplateResource(String template) throws IOException {
        String name = "src/templates/services/" + template
        AbstractResource templateFile = new FileSystemResource(new File(basedir, name).getAbsoluteFile())
        if (!templateFile.exists()) {
            templateFile = new FileSystemResource(new File(getPluginDir(), name).getAbsoluteFile())
        }

        return templateFile
    }

    protected boolean canWrite(File testFile) {
        if (overwrite || !testFile.exists()) {
            return true
        }

        try {
            String relative = makeRelativeIfPossible(testFile.getAbsolutePath(), basedir)
            String response = GrailsConsole.getInstance().userInput("File " + relative + " already exists. Overwrite?", ["y", "n", "a"] as String[])
            overwrite = overwrite || "a".equals(response)
            return overwrite || "y".equals(response)
        }
        catch (Exception e) {
            // failure to read from standard in means we're probably running from an automation tool like a build server
            return true
        }
    }

    protected String makeRelativeIfPossible(String fileName, String base) throws IOException {
        if (StringUtils.hasLength(base)) {
            fileName = StringGroovyMethods.minus(fileName, new File(base).getCanonicalPath())
        }
        return fileName
    }

    protected File getPluginDir() throws IOException {
        GrailsPluginInfo info = GrailsPluginUtils.getPluginBuildSettings().getPluginInfoForName("fnui-model")
        return info.getDescriptor().getFile().getParentFile()
    }
}
