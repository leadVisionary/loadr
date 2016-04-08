package com.visionarysoftwaresolutions.loadr

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient
import com.amazonaws.services.dynamodbv2.model.PutItemRequest

import groovyx.gpars.actor.StaticDispatchActor
import groovyx.gpars.group.DefaultPGroup
import groovyx.gpars.scheduler.FJPool
import org.slf4j.Logger

import java.util.function.Function
import java.util.function.Supplier

final class DynamoDBPublishers<T> implements Supplier<StaticDispatchActor<T>> {
    private final Collection<StaticDispatchActor<T>> actors

    DynamoDBPublishers(final int numPublishers,
                       final Logger log,
                       final Function<T, PutItemRequest> transformer) {
        def publisherPool = new DefaultPGroup(new FJPool(numPublishers))
        actors = []
        numPublishers.times {
            final AmazonDynamoDB db = new AmazonDynamoDBAsyncClient();
            final StaticDispatchActor<T> publisher = new PublishingActor(new DynamoDBPublishCommand(db, log, transformer))
            publisher.parallelGroup = publisherPool
            actors << publisher
        }
    }


    @Override
    StaticDispatchActor<T> get() {
        final int nextIndex = (int) (Math.random() * (actors.size() - 1)) + 1
        final StaticDispatchActor<T> publisher = actors[nextIndex]
        if (!publisher.isActive()) {
            publisher.start()
        }
        publisher
    }
}
