package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.api.CommandSupplier
import com.visionarysoftwaresolutions.loadr.api.Command
import groovyx.gpars.actor.StaticDispatchActor
import java.util.function.Supplier

class CommandBasedActorSupplierSpec extends spock.lang.Specification {
    def "rejects null arguments"() {
        when: "I try to create with null"
            new CommandBasedActorSupplier(null)
        then: "I get IllegalArgumentException"
            IllegalArgumentException e = thrown()
    }

    def "can get a PublishingActor with a DynamoDBPublish command"() {
        given: "A Supplier of Command"
            CommandSupplier<?> comSup = Mock(CommandSupplier)
        and: "A CommandBasedActorSupplier"
            final Supplier<StaticDispatchActor<?>> sup = new CommandBasedActorSupplier<?>(comSup)
        when: "I get the actor from it"
            final StaticDispatchActor<?> actor = sup.get()
        then: "the supplier vends a Command"
            1 * comSup.getCommand() >> Mock(Command)
        and: "the Actor is a CommandingActor"
            actor instanceof CommandingActor
    }
}
