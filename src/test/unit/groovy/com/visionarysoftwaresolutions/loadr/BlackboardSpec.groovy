package com.visionarysoftwaresolutions.loadr

import groovyx.gpars.actor.Actor
import groovyx.gpars.actor.StaticDispatchActor

import java.util.function.Supplier

class BlackboardSpec extends spock.lang.Specification {
    def "reject null Actor supplier"() {
        given: "a null Supplier"
            Supplier<StaticDispatchActor<?>> sup = null
        when: "I try to create a Blackboard"
            new Blackboard<?>(sup)
        then: "an IllegalArgumentException is thrown"
            IllegalArgumentException e = thrown()
    }

    def "stores item and notifies actors on save"() {
        given: "a Supplier"
            Supplier<StaticDispatchActor<String>> sup = Mock(Supplier)
        and: "an Actor to be supplied for notification"
            StaticDispatchActor<String> reactor = Mock(StaticDispatchActor)
        and: "an item to save"
            String item = "test"
        when: "I try to create a Blackboard"
            Blackboard<String> toTest = new Blackboard<?>(sup)
        and: "I try to save an item"
            toTest.save(item)
        then: "the supplier supplies"
            1 * sup.get() >> reactor
        and: "the subscriber is notified"
            1 * reactor.send(item)
    }

    def "stops actors on close"() {
        given: "a Supplier"
            Supplier<StaticDispatchActor<String>> sup = Mock(Supplier)
        and: "an Actor to be supplied for notification"
            StaticDispatchActor<String> reactor = Mock(StaticDispatchActor)
        and: "an item to save"
            String item = "test"
        when: "I try to create a Blackboard"
            Blackboard<String> toTest = new Blackboard<?>(sup)
        and: "I try to save an item"
            toTest.save(item)
        and: "I then erase the blackboard"
            toTest.close()
        then: "the supplier supplies"
            1 * sup.get() >> reactor
        and: "the subscriber is notified"
            1 * reactor.send(item)
        and: "the reactor receives the stop message"
            1 * reactor.send(Actor.STOP_MESSAGE)
    }
}
