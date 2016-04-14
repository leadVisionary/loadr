package com.visionarysoftwaresolutions.loadr

import com.visionarysoftwaresolutions.loadr.actors.ActorBasedFileProcessingCommandSupplier
import com.visionarysoftwaresolutions.loadr.api.Command
import org.slf4j.Logger

import java.util.function.Function

public final class Loader {
    public static <T, U> void loadFromFileWithDelimiterAndPublishers(final File source,
                                                                  final int publishers,
                                                                  final Logger log,
                                                                  final Function<T, U> mapper,
                                                                  final Function<String, T> stringTransform) {
        def supplier = new ActorBasedFileProcessingCommandSupplier<T, U>(log, publishers, mapper, stringTransform)
        Command<File> command = supplier.get()
        command.execute(source)
    }
}
