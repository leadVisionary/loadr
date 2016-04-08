package com.visionarysoftwaresolutions.loadr


import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import groovyx.gpars.actor.Actor
import groovyx.gpars.actor.StaticDispatchActor
import org.slf4j.Logger

import java.util.function.Function
import java.util.function.Supplier

public final class Loader {
    public static <T> void loadFromFileWithDelimiterAndPublishers(final File source,
                                                                  final int publishers,
                                                                  final Logger log,
                                                                  final Function<T, PutItemRequest> mapper,
                                                                  final Function<String, T> stringTransform) {
        final Supplier<Collection<Actor>> savers = new DynamoDBPublishers(publishers, log, mapper)
        final Collection<Actor> saverActors = savers.get()
        saverActors*.start()
        final StaticDispatchActor<String> transformer = new StringTransformingDispatcher(savers, log, stringTransform)
        transformer.start()
        final FileScanningActor reader = new FileScanningActor(transformer)
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
