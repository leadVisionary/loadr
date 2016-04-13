package com.visionarysoftwaresolutions.loadr


import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import com.visionarysoftwaresolutions.loadr.actors.FileScanningActor
import com.visionarysoftwaresolutions.loadr.actors.RandomlySelectingActorPool
import com.visionarysoftwaresolutions.loadr.actors.StringTransformingActor
import com.visionarysoftwaresolutions.loadr.dynamodb.DynamoDBPublisherSupplier
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
        final Supplier<StaticDispatchActor<T>> sup = new DynamoDBPublisherSupplier<T>(log, mapper)
        final Supplier<StaticDispatchActor<T>> savers = new RandomlySelectingActorPool(publishers, sup)
        final StaticDispatchActor<String> transformer = new StringTransformingActor(savers, log, stringTransform)
        transformer.start()
        new Loader(new FileScanningActor(transformer), source).load()
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
