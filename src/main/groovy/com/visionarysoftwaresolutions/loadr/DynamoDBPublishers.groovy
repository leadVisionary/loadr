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

final class DynamoDBPublishers<T> implements Supplier<Collection<StaticDispatchActor<T>>> {
    private final int numPublishers
    private final Logger log
    private final Function<T, PutItemRequest> transformer

    DynamoDBPublishers(final int numPublishers,
                       final Logger log,
                       final Function<T, PutItemRequest> transformer) {
        this.numPublishers = numPublishers
        this.log = log
        this.transformer = transformer
    }


    @Override
    Collection<StaticDispatchActor<T>> get() {
        def publisherPool = new DefaultPGroup(new FJPool(numPublishers))
        def actors = []
        numPublishers.times {
            final AmazonDynamoDB db = new AmazonDynamoDBAsyncClient();
            final StaticDispatchActor<T> publisher = new PublishingActor(new DynamoDBPublishCommand(db, log, transformer))
            publisher.parallelGroup = publisherPool
            actors << publisher
        }
        actors
    }
}
