package fnui.model

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType

class AuiLink {
    final String kind
    final Map<String,Object> data
    private final Closure<String> _params

    AuiLink(String kind, Map<String,Object> target,
            @ClosureParams(value = SimpleType.class, options = "java.util.String")
                    Closure<String> params = { varName -> '' }) {
        this.kind = kind
        this.data = target ?: [:]
        this._params = params
    }

    String generateParams(String varName) {
        _params.call(varName)
    }

    def propertyMissing(String name) {
        data[name]
    }

    def propertyMissing(String name, def arg) {
        data[name] = arg
    }

    String toString() {
        "Link[kind:${kind}, target:${data}, params:${_params.call('var')}"
    }
}
