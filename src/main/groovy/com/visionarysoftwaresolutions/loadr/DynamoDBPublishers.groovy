package com.visionarysoftwaresolutions.loadr

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model.PutItemRequest

import groovyx.gpars.actor.StaticDispatchActor
import groovyx.gpars.group.DefaultPGroup
import groovyx.gpars.scheduler.FJPool

import java.util.function.Function
import java.util.function.Supplier

final class DynamoDBPublishers<T> implements Supplier<Collection<StaticDispatchActor<T>>> {
    private final Collection<StaticDispatchActor<T>> actors

    DynamoDBPublishers(final int numPublishers,
                       final AmazonDynamoDB db,
                       final File log,
                       final Function<T, PutItemRequest> transformer) {
        def publisherPool = new DefaultPGroup(new FJPool(numPublishers))
        actors = []
        numPublishers.times {
            final StaticDispatchActor<T> publisher = new DynamoDBPublisher(db, log, transformer)
            publisher.parallelGroup = publisherPool
            actors << publisher
        }
    }


    @Override
    Collection<StaticDispatchActor<T>> get() { actors }
}
