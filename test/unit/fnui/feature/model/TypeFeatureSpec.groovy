package fnui.feature.model

import spock.lang.Specification
import spock.lang.Unroll

class TypeFeatureSpec extends Specification {

    @Unroll('Check getClassDeclaration for type #definition')
    def "getClassDefinition generates definition for variable"() {
        expect:
        t(propertyName).classDeclaration == definition

        where:
        propertyName | definition
        'a'          | 'fnui.feature.model.TypeFeatureSpec$A'
        'b'          | 'java.util.List<fnui.feature.model.TypeFeatureSpec$A>'
        'c'          | 'fnui.feature.model.TypeFeatureSpec$A[]'
        'd'          | 'java.util.List<fnui.feature.model.TypeFeatureSpec$A[]>'
    }

    private TypeFeature t(String n) {
        def type = A.getMethod("get${n.toUpperCase()}").genericReturnType
        new TypeFeature(new FeatureModel(), type)
    }

    class A {
        A a
        List<A> b
        A[] c
        List<A[]> d
    }
}


