package com.visionarysoftwaresolutions.loadr

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import groovyx.gpars.actor.Actor
import groovyx.gpars.actor.StaticDispatchActor

import java.util.function.Function
import java.util.function.Supplier

public final class Loader {
    public static <T> void loadFromFileWithDelimiterAndPublishers(final File source,
                                                           final int publishers,
                                                           final File logFile,
                                                           final Function<T, PutItemRequest> mapper,
                                                           final Function<String, T> stringTransform) {
        final AmazonDynamoDB db = new AmazonDynamoDBAsyncClient();
        final Supplier<Collection<Actor>> savers = new DynamoDBPublishers(publishers, db, logFile, mapper)
        final Collection<Actor> saverActors = savers.get()
        saverActors*.start()
        final StaticDispatchActor<String> transformer = new StringTransformingDispatcher(saverActors, logFile, stringTransform)
        transformer.start()
        final java.io.FileReader reader = new java.io.FileReader(transformer)
        new Loader(reader, source).load()
        reader.join()
    }

    private final StaticDispatchActor<File> reader
    private final File source

    private Loader(final StaticDispatchActor<File> reader, final File source) {
        this.reader = reader
        this.source = source
    }

    private void load() {
        if(!reader.active) {
            reader.start()
        }
        reader << source
        reader.join()
    }
}
