package com.visionarysoftwaresolutions.loadr.dynamodb

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import com.visionarysoftwaresolutions.loadr.actors.PublishingActor
import groovyx.gpars.actor.StaticDispatchActor
import org.slf4j.Logger

import java.util.function.Function
import java.util.function.Supplier

final class DynamoDBPublisherSupplier<T> implements Supplier<StaticDispatchActor<T>> {
    private final Logger log
    private final Function<T, PutItemRequest> transformer

    DynamoDBPublisherSupplier(final Logger log, final Function<T, PutItemRequest> transformer) {
        if (log == null) {
            throw new IllegalArgumentException("should not get null log")
        }
        this.log = log
        if (transformer == null) {
            throw new IllegalArgumentException("should not get null transformer")
        }
        this.transformer = transformer
    }

    @Override
    StaticDispatchActor<T> get() {
        final AmazonDynamoDB db = new AmazonDynamoDBAsyncClient();
        new PublishingActor(new DynamoDBCommand(db, log, transformer))
    }
}
