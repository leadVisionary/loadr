package com.visionarysoftwaresolutions.loadr

import com.visionarysoftwaresolutions.loadr.actors.CommandBasedActorSupplier
import org.slf4j.Logger

import java.util.function.Function

public final class Loader {
    public static <T, U> void loadFromFileWithDelimiterAndPublishers(final File source,
                                                                  final int publishers,
                                                                  final Logger log,
                                                                  final Function<T, U> mapper,
                                                                  final Function<String, T> stringTransform) {
        new LoadFromFileViaActorsCommand(new CommandBasedActorSupplier(publishers, log, mapper, stringTransform)).execute(source)
    }
}
