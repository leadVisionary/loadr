package com.visionarysoftwaresolutions.loadr

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import groovy.json.JsonOutput
import groovyx.gpars.actor.StaticDispatchActor

import java.time.Instant
import java.util.function.Function

final class DynamoDBPublisher<T> extends StaticDispatchActor<T> {
    private final AmazonDynamoDB client
    private final File logFile
    private final Function<T, PutItemRequest> transformer

    DynamoDBPublisher(final AmazonDynamoDB client, final File logFile, Function<T, PutItemRequest> transformer) {
        this.client = client
        this.logFile = logFile
        this.transformer = transformer
    }

    @Override
    void onMessage(final T id) {
        handleNextMessage(id)
    }

    void handleNextMessage(final T id) {
        try {
            logFile.append("${Instant.now()}: Writing ${id} with size ${JsonOutput.toJson(id).bytes.length} \n")
            client.putItem(transformer.apply(id))
        } catch (final Exception e) {
            final String message = String.format("%s: Failed to insert %s because %s %n", new Instant(), id, e.message)
            logFile.append(message)
        }
    }
}
