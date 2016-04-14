package com.visionarysoftwaresolutions.loadr.dynamodb

import com.visionarysoftwaresolutions.loadr.actors.PublishingActor
import com.visionarysoftwaresolutions.loadr.api.Command
import groovyx.gpars.actor.StaticDispatchActor
import java.util.function.Supplier

class DynamoDBPublisherSupplierSpec extends spock.lang.Specification {
    def "rejects null arguments"() {
        when: "I try to create with null"
            new DynamoDBPublisherSupplier(null)
        then: "I get IllegalArgumentException"
            IllegalArgumentException e = thrown()
    }

    def "can get a PublishingActor with a DynamoDBPublish command"() {
        given: "A Supplier of Command"
            Supplier<Command<?>> comSup = Mock()
        and: "A DynamoDBPublisherSupplier"
            final Supplier<StaticDispatchActor<?>> sup = new DynamoDBPublisherSupplier<?>(comSup)
        when: "I get the actor from it"
            final StaticDispatchActor<?> actor = sup.get()
        then: "the supplier vends a Command"
            1 * comSup.get() >> Mock(Command)
        and: "the Actor is a PublishingActor"
            actor instanceof PublishingActor
    }
}
