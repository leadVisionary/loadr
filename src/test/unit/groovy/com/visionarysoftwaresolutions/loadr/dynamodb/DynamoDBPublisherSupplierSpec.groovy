package com.visionarysoftwaresolutions.loadr.dynamodb

import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import com.visionarysoftwaresolutions.loadr.actors.PublishingActor
import groovyx.gpars.actor.StaticDispatchActor
import org.slf4j.Logger

import java.util.function.Function
import java.util.function.Supplier

class DynamoDBPublisherSupplierSpec extends spock.lang.Specification {
    def "rejects null arguments"() {
        when: "I try to create with null"
            new DynamoDBPublisherSupplier(log, transformer)
        then: "I get IllegalArgumentException"
            IllegalArgumentException e = thrown()
        where: log          | transformer
               null         | Mock(Function)
               Mock(Logger) | null
    }

    def "can get a PublishingActor with a DynamoDBPublish command"() {
        given: "A logger"
            final Logger log = Mock(Logger)
        and: "a function that applies transforms"
            final Function<?, PutItemRequest> f = Mock(Function)
        and: "A DynamoDBPublisherSupplier"
            final Supplier<StaticDispatchActor<?>> sup = new DynamoDBPublisherSupplier<?>(log, f)
        when: "I get the actor from it"
            final StaticDispatchActor<?> actor = sup.get()
        then: "the Actor is a PublishingActor"
            actor instanceof PublishingActor
    }
}
