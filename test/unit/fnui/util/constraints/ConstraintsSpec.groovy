package fnui.util.constraints

import spock.lang.Specification

class ConstraintsSpec extends Specification {

    def 'getBoolean: parse boolean values in constraints'() {
        def constraints = new Constraints([
                boolean    : true,
                trueString : 'true',
                falseString: 'False',
                nullValue  : null,
                wrong      : 29])

        expect:
        constraints.getBoolean(propertyName, defaultValue) == value

        where:
        propertyName  | defaultValue | value
        'boolean'     | false        | true
        'trueString'  | false        | true
        'falseString' | true         | false
        'nullValue'   | true         | true
        'wrong'       | false        | false
        'notExisting' | null         | null
    }
}
