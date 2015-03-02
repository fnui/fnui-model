<%= packageName ? "package $packageName" : '' %>

import fnui.core.annotations.UserInterface
import grails.transaction.Transactional

@Transactional
@UserInterface
class ${className}Service {

    @Transactional(readOnly = true)
    List<${className}> list${className}s() {
        ${className}.list()
    }

    @Transactional(readOnly = true)
    ${className} show${className}(${className} ${propertyName}) {
        ${propertyName}
    }

    ${className} create${className}(${className} new${className}) {
        new${className}.save(flush:true) ?: new${className}
    }

    ${className} update${className}(${className} ${propertyName}) {
        ${propertyName}.save(flush:true) ?: ${propertyName}
    }

    void delete${className}(${className} ${propertyName}) {
        ${propertyName}.delete()
    }
}