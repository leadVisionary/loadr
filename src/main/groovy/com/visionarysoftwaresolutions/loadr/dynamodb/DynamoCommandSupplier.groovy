package com.visionarysoftwaresolutions.loadr.dynamodb

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import com.visionarysoftwaresolutions.loadr.api.Command

import org.slf4j.Logger

import java.util.function.Function
import java.util.function.Supplier

final class DynamoCommandSupplier<T> implements Supplier<Command<T>> {
    private final Logger log
    private final Function<T, PutItemRequest> func

    DynamoCommandSupplier(final Logger log, final Function<T, PutItemRequest> func) {
        if (log == null) {
            throw new IllegalArgumentException("should not get null log")
        }
        this.log = log
        if (func == null) {
            throw new IllegalArgumentException("should not get null func")
        }
        this.func = func
    }

    @Override
    Command<T> get() {
        final AmazonDynamoDB db = new AmazonDynamoDBAsyncClient();
        new DynamoDBCommand(db, log, func)
    }
}
