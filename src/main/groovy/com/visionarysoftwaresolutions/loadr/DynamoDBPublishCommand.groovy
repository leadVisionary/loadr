package com.visionarysoftwaresolutions.loadr

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import com.visionarysoftwaresolutions.loadr.api.PublishCommand
import groovy.json.JsonOutput
import org.slf4j.Logger

import java.time.Instant
import java.util.function.Function

final class DynamoDBPublishCommand<T> implements PublishCommand<T> {
    private final AmazonDynamoDB client
    private final Logger logFile
    private final Function<T, PutItemRequest> transformer

    DynamoDBPublishCommand(final AmazonDynamoDB client,
                           final Logger logFile,
                           final Function<T, PutItemRequest> transformer) {
        if (client == null) {
            throw new IllegalArgumentException("should not get null client")
        }
        this.client = client

        if (logFile == null) {
            throw new IllegalArgumentException("should not get null file")
        }
        this.logFile = logFile

        if(transformer == null) {
            throw new IllegalArgumentException("should not get null transformer")
        }
        this.transformer = transformer
    }

    @Override
    void execute(final T toPublish) {
        try {
            final String message = String.format("%s: Writing %s with size %d %n", Instant.now(), toPublish, JsonOutput.toJson(toPublish).bytes.length)
            logFile.debug(message)
            client.putItem(transformer.apply(toPublish))
        } catch (final Exception e) {
            final String message = String.format("%s: Failed to insert %s because %s %n", Instant.now(), toPublish, e.message)
            logFile.error(message)
        }
    }
}
