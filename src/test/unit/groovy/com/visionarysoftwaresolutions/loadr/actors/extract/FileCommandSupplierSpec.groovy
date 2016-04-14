package com.visionarysoftwaresolutions.loadr.actors.extract

import com.visionarysoftwaresolutions.loadr.api.Command
import groovyx.gpars.actor.StaticDispatchActor

import java.util.function.Supplier

class FileCommandSupplierSpec extends spock.lang.Specification {
    def "rejects null inputs"() {
        when: "I try to create with null"
            new FileCommandSupplier(null)
        then: "An IllegalArgumentException is thrown"
            IllegalArgumentException e = thrown()
    }

    def "throws IllegalStateException when Supplier yields null"() {
        given: "a Supplier"
            Supplier<StaticDispatchActor<String>> sup = Mock(Supplier)
        and: "I try to create with supplier"
            FileCommandSupplier toTest = new FileCommandSupplier(sup)
        when: "I ask for a command"
            Command<File> com = toTest.get()
        then: "the supplier gives null"
            1 * sup.get() >> null
        and: "IllegalStateException is thrown"
            IllegalStateException e = thrown()

    }

    def "can get a SendLinesOfFileToActorCommand"() {
        given: "a Supplier"
            Supplier<StaticDispatchActor<String>> sup = Mock(Supplier)
        and: "I try to create with supplier"
            FileCommandSupplier toTest = new FileCommandSupplier(sup)
        when: "I ask for a command"
            Command<File> com = toTest.get()
        then: "the supplier gives and Actor"
            1 * sup.get() >> Mock(StaticDispatchActor)
        and: "the Command is a SendLinesOfFileToActorCommand"
            com instanceof SendLinesOfFileToActorCommand

    }
}
