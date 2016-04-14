package com.visionarysoftwaresolutions.loadr.actors

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import com.visionarysoftwaresolutions.loadr.api.Command
import com.visionarysoftwaresolutions.loadr.dynamodb.DynamoDBCommand
import org.slf4j.Logger

import java.util.function.Function

final class DynamoCommandSupplier<T> extends CommandSupplier<T> {
    private final Logger log
    private final Function<T, PutItemRequest> transformer

    DynamoCommandSupplier(final Logger log, final Function<T, PutItemRequest> transformer) {
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
    Command<T> getCommand() {
        final AmazonDynamoDB db = new AmazonDynamoDBAsyncClient();
        new DynamoDBCommand(db, log, transformer)
    }
}
