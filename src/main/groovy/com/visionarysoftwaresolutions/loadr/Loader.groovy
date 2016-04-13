package com.visionarysoftwaresolutions.loadr

import com.visionarysoftwaresolutions.loadr.actors.CommandBasedActorSupplier
import groovyx.gpars.actor.StaticDispatchActor
import org.slf4j.Logger

import java.util.function.Function
import java.util.function.Supplier

public final class Loader {
    public static <T, U> void loadFromFileWithDelimiterAndPublishers(final File source,
                                                                  final int publishers,
                                                                  final Logger log,
                                                                  final Function<T, U> mapper,
                                                                  final Function<String, T> stringTransform) {
        final Supplier<StaticDispatchActor<File>> c =
                new CommandBasedActorSupplier(publishers, log, mapper, stringTransform)
        new Loader(c.get(), source).load()
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
