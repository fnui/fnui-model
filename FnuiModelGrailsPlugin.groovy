class FnuiModelGrailsPlugin {
    def version = "0.1"
    def grailsVersion = "2.4 > *"

    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    def loadAfter = ['fnui-core']

    def title = "FnUI Model Plugin" // Headline display name of the plugin
    def author = "Florian Freudenberg"
    def authorEmail = "flo@freudenberg.berlin"
    def description = '''\
This plugin provides the tools for the feature extraction and model generation step of the FnUI
toolchain.
'''

    def documentation = "https://github.com/fnui/fnui-model"

    def license = "APACHE"

    def scm = [ url: "https://github.com/fnui/fnui-model" ]
}
