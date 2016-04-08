package com.visionarysoftwaresolutions.loadr

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import groovyx.gpars.actor.StaticDispatchActor
import org.slf4j.Logger

import java.util.function.Function
import java.util.function.Supplier

final class DynamoDBPublisherSupplier<T> implements Supplier<StaticDispatchActor<T>> {
    private final Logger log
    private final Function<T, PutItemRequest> transformer

    DynamoDBPublisherSupplier(final Logger log, final Function<T, PutItemRequest> transformer) {
        this.log = log
        this.transformer = transformer
    }

    @Override
    StaticDispatchActor<T> get() {
        final AmazonDynamoDB db = new AmazonDynamoDBAsyncClient();
        new PublishingActor(new DynamoDBPublishCommand(db, log, transformer))
    }
}
