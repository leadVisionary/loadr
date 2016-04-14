package com.visionarysoftwaresolutions.loadr

import com.visionarysoftwaresolutions.loadr.actors.ActorBasedFileProcessingCommandSupplier
import org.slf4j.Logger

import java.util.function.Function

class ActorBasedFileProcessingCommandSupplierSpec extends spock.lang.Specification {
    def "rejects nulls and negative publishers"() {
        when: "constructed with nulls or negative publishers"
            new ActorBasedFileProcessingCommandSupplier(l, p, m, t)
        then: "IllegalArgumentException is thrown"
            IllegalArgumentException e = thrown()
        where:    l         | p  | m              | t
               null         | 1  | Mock(Function) | Mock(Function)
               Mock(Logger) | -1 | Mock(Function) | Mock(Function)
               Mock(Logger) | 1  | null           | Mock(Function)
               Mock(Logger) | 1  | Mock(Function) | null
    }
}
