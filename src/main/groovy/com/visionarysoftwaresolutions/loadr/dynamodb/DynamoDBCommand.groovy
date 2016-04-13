package com.visionarysoftwaresolutions.loadr.dynamodb

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import com.visionarysoftwaresolutions.loadr.api.Command
import groovy.json.JsonOutput
import org.slf4j.Logger

import java.time.Instant
import java.util.function.Function

final class DynamoDBCommand<T> implements Command<T> {
    private final AmazonDynamoDB client
    private final Logger logFile
    private final Function<T, PutItemRequest> transformer

    DynamoDBCommand(final AmazonDynamoDB client,
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
    void execute(final T parameter) {
        try {
            final String message = String.format("%s: Writing %s with size %d %n", Instant.now(), parameter, JsonOutput.toJson(parameter).bytes.length)
            logFile.debug(message)
            client.putItem(transformer.apply(parameter))
        } catch (final Exception e) {
            final String message = String.format("%s: Failed to insert %s because %s %n", Instant.now(), parameter, e.message)
            logFile.error(message)
        }
    }
}
