package com.visionarysoftwaresolutions.loadr

import org.slf4j.Logger

import java.util.function.Function
import java.util.function.Supplier

class StringTransformingActorIntegrationSpec extends spock.lang.Specification {
    def "rejects null inputs"() {
        when: "I try to construct with a null"
            new StringTransformingActor(r, l, t)
        then: "an IllegalArgumentException is thrown"
            IllegalArgumentException e = thrown()
        where: r                                      | l            | t
               null                                   | Mock(Logger) | Mock(Function)
               new Blackboard(Mock(Supplier))         | null         | Mock(Function)
               new Blackboard(Mock(Supplier))         | Mock(Logger) | null
    }
}
