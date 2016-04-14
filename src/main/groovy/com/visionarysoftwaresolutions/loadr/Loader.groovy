package com.visionarysoftwaresolutions.loadr

import com.visionarysoftwaresolutions.loadr.actors.BlackboardSupplier
import com.visionarysoftwaresolutions.loadr.actors.CommandBasedActorSupplier
import com.visionarysoftwaresolutions.loadr.actors.FileCommandSupplier
import com.visionarysoftwaresolutions.loadr.actors.StringTransformCommandSupplier
import com.visionarysoftwaresolutions.loadr.actors.SubscriberSupplier
import com.visionarysoftwaresolutions.loadr.actors.TransformingActorSupplier
import com.visionarysoftwaresolutions.loadr.api.CloseableRepository
import com.visionarysoftwaresolutions.loadr.api.Command
import org.slf4j.Logger

import java.util.function.Function
import java.util.function.Supplier

public final class Loader {
    public static <T, U> void loadFromFileWithDelimiterAndPublishers(final File source,
                                                                  final int publishers,
                                                                  final Logger log,
                                                                  final Function<T, U> mapper,
                                                                  final Function<String, T> stringTransform) {
        final Supplier<CloseableRepository<T>> blah = new BlackboardSupplier<T, U>(new SubscriberSupplier(publishers, log, mapper))
        final Supplier<Command<String>> sup = new StringTransformCommandSupplier<>(blah, log, stringTransform)
        final Supplier<Command<File>> fSup = new FileCommandSupplier(new TransformingActorSupplier(sup))
        new LoadFromFileViaActorsCommand(new CommandBasedActorSupplier(fSup)).execute(source)
    }
}
