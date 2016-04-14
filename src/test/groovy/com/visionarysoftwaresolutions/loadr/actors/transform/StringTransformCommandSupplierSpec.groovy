package com.visionarysoftwaresolutions.loadr.actors.transform

import com.visionarysoftwaresolutions.loadr.api.CloseableRepository
import com.visionarysoftwaresolutions.loadr.api.Command
import org.slf4j.Logger

import java.util.function.Function
import java.util.function.Supplier

class StringTransformCommandSupplierSpec extends spock.lang.Specification {

    def "rejects null inputs"() {
        when: "I try to construct with null"
            new StringTransformCommandSupplier(sup, log, trans)
        then: "an IllegalArgumentException is thrown"
            IllegalArgumentException e = thrown()
        where: sup            | log          | trans
               null           | Mock(Logger) | Mock(Function)
               Mock(Supplier) | null         | Mock(Function)
               Mock(Supplier) | Mock(Logger) | null
    }

    def "throws IllegalStateException on null from Supplier"() {
        given: "A Logger"
            Logger log = Mock(Logger)
        and: "a transform"
            Function func = Mock(Function)
        and: "A Supplier"
            Supplier<CloseableRepository<?>> sup = Mock(Supplier)
        and: "A StringTransformCommandSupplier to toest"
            StringTransformCommandSupplier<?> toTest = new StringTransformCommandSupplier(sup, log, func)
        when: "I try to get"
            Command<String> result = toTest.get()
        then: "the supplier is invoked"
            1 * sup.get() >> null
        and: "an IllegalStateException is thrown"
            IllegalStateException e = thrown()
    }

    def "gets TransformStringAndSaveToRepositoryCommand"() {
        given: "A Logger"
            Logger log = Mock(Logger)
        and: "a transform"
            Function func = Mock(Function)
        and: "A Supplier"
            Supplier<CloseableRepository<?>> sup = Mock(Supplier)
        and: "A StringTransformCommandSupplier to toest"
            StringTransformCommandSupplier<?> toTest = new StringTransformCommandSupplier(sup, log, func)
        when: "I try to get"
            Command<String> result = toTest.get()
        then: "the supplier is invoked"
            1 * sup.get() >> Mock(CloseableRepository)
        and: "the result is TransformStringAndSaveToRepositoryCommand"
        result instanceof TransformStringAndSaveToRepositoryCommand
    }

}
