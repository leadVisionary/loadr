package com.visionarysoftwaresolutions.loadr.actors.store

import groovyx.gpars.actor.StaticDispatchActor

import java.util.function.Supplier

class RandomlySelectingActorPoolSpec extends spock.lang.Specification {

    def "rejects negative number of publishers or null supplier"() {
        when: "I try to create with invalid input"
            new RandomlySelectingActorPool<?>(numPub, sup)
        then: "an IllegalArgumentException is thrown"
            IllegalArgumentException e = thrown()
        where: numPub | sup
                -1    | Mock(Supplier)
                 1    | null

    }

    def "can create actors"() {
        given: "a supplier of Actors"
            final Supplier<StaticDispatchActor<?>> sup = Mock(Supplier)
        and: "a mock dispatch actor"
            final StaticDispatchActor<String> dispatcher = Mock(StaticDispatchActor)
        and: "a number of actors desired"
            final int pubs = 1
        when: "I make a RandomlySelectingActorPool"
            final Supplier<StaticDispatchActor<?>> toTest = new RandomlySelectingActorPool<?>(pubs, sup)
        and: "I ask for an actor"
            final StaticDispatchActor<?> actor = toTest.get()
        then: "the supplier is invoked"
            1 * sup.get() >>  dispatcher
        and: "the parallel group is set"
            1 * dispatcher.setParallelGroup(_)
        and: "the actor is started"
            1 * dispatcher.start()
        and: "the expected actor is returned"
            actor == dispatcher
    }
}
