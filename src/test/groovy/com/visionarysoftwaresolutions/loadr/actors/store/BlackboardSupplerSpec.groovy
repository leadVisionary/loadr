package com.visionarysoftwaresolutions.loadr.actors.store

import com.visionarysoftwaresolutions.loadr.api.CloseableRepository
import groovyx.gpars.actor.StaticDispatchActor

import java.util.function.Supplier

class BlackboardSupplerSpec extends spock.lang.Specification {

    def "rejects null"() {
        when: "I try to construct with null"
            new BlackboardSupplier(null)
        then: "IllegalArgumentException is thrown"
            IllegalArgumentException e = thrown()
    }

    def "can construct with Supplier"() {
        given: "A Supplier of Actor"
            Supplier<StaticDispatchActor<?>> sup = Mock(Supplier)
        and: "A BlackboardSupplier to test"
            BlackboardSupplier toTest = new BlackboardSupplier(sup)
        when: "I try to get"
            CloseableRepository<?> result = toTest.get()
        then: "I get a blackboard"
            result instanceof  Blackboard
        and: "the supplier is passed along"
            0 * sup.get()
    }
}
