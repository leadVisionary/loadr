package com.visionarysoftwaresolutions.loadr

import com.visionarysoftwaresolutions.loadr.actors.ActorBasedFileProcessingCommandSupplier
import com.visionarysoftwaresolutions.loadr.actors.extract.LoadFromFileViaActorsCommand
import com.visionarysoftwaresolutions.loadr.api.Command
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

    def "can get a LoadFromFileViaActorsCommand"() {
        given: "A Logger"
            Logger l = Mock(Logger)
        and: "a number of publishers"
            int p = 1
        and: "a mapping function"
            Function f = Mock(Function)
        and: "a transformation function"
            Function t = Mock(Function)
        and: "A ActorBasedFileProcessingCommandSupplier to test"
            ActorBasedFileProcessingCommandSupplier toTest = new ActorBasedFileProcessingCommandSupplier(l,p,f,t)
        when: "I get from ActorBasedFileProcessingCommandSupplier"
            Command<File> result = toTest.get()
        then: "the result is a LoadFromFileViaActorsCommand"
            result instanceof LoadFromFileViaActorsCommand
    }
}
