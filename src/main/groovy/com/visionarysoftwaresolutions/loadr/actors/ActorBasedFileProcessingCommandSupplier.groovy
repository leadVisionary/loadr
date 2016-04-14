package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.actors.extract.FileCommandSupplier
import com.visionarysoftwaresolutions.loadr.actors.extract.LoadFromFileViaActorsCommand
import com.visionarysoftwaresolutions.loadr.actors.store.BlackboardSupplier
import com.visionarysoftwaresolutions.loadr.actors.store.SubscriberSupplier
import com.visionarysoftwaresolutions.loadr.actors.transform.StringTransformCommandSupplier
import com.visionarysoftwaresolutions.loadr.api.CloseableRepository
import com.visionarysoftwaresolutions.loadr.api.Command
import com.visionarysoftwaresolutions.loadr.dynamodb.DynamoCommandSupplier
import org.slf4j.Logger

import java.util.function.Function
import java.util.function.Supplier

public final class ActorBasedFileProcessingCommandSupplier<T,U> implements Supplier<Command<File>> {
    private final Logger log
    private final int publishers
    private final Function<T, U> mapper
    private final Function<String, T> stringTransform

    public ActorBasedFileProcessingCommandSupplier(final Logger log,
                                                   final int publishers,
                                                   final Function<T, U> mapper,
                                                   final Function<String, T> stringTransform
                                                   ) {
        if (log == null) {
            throw new IllegalArgumentException("log should not be null")
        }
        this.log = log
        if (publishers < 0) {
            throw new IllegalArgumentException("should not get publishers < 0")
        }
        this.publishers = publishers
        if (mapper == null) {
            throw new IllegalArgumentException("mapper should not be null")
        }
        this.mapper = mapper
        if (stringTransform == null) {
            throw new IllegalArgumentException("stringTransform should not be null")
        }
        this.stringTransform = stringTransform
    }

    @Override
    Command<File> get() {
        def subscriberSupplier = new SubscriberSupplier(publishers, new DynamoCommandSupplier(log, mapper))
        final Supplier<CloseableRepository<T>> blah = new BlackboardSupplier<T>(subscriberSupplier)
        final Supplier<Command<String>> sup = new StringTransformCommandSupplier<>(blah, log, stringTransform)
        final Supplier<Command<File>> fSup = new FileCommandSupplier(new CommandBasedActorSupplier(sup))

        def supplier = new CommandBasedActorSupplier(fSup)

        def command = new LoadFromFileViaActorsCommand(supplier)
        command
    }
}
