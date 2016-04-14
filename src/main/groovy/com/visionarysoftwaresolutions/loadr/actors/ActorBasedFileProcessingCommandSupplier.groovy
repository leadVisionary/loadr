package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.actors.extract.FileCommandSupplier
import com.visionarysoftwaresolutions.loadr.actors.extract.LoadFromFileViaActorsCommand
import com.visionarysoftwaresolutions.loadr.actors.store.BlackboardSupplier
import com.visionarysoftwaresolutions.loadr.actors.store.RandomlySelectingActorPool
import com.visionarysoftwaresolutions.loadr.actors.transform.StringTransformCommandSupplier
import com.visionarysoftwaresolutions.loadr.api.CloseableRepository
import com.visionarysoftwaresolutions.loadr.api.Command
import com.visionarysoftwaresolutions.loadr.dynamodb.DynamoCommandSupplier
import groovyx.gpars.actor.StaticDispatchActor
import org.slf4j.Logger

import java.util.function.Function
import java.util.function.Supplier

public final class ActorBasedFileProcessingCommandSupplier<T,U> implements Supplier<Command<File>> {
    private final Logger log
    private final int publishers
    private final Function<T, U> load
    private final Function<String, T> transform

    public ActorBasedFileProcessingCommandSupplier(final Logger log,
                                                   final int publishers,
                                                   final Function<T, U> load,
                                                   final Function<String, T> transform
                                                   ) {
        if (log == null) {
            throw new IllegalArgumentException("log should not be null")
        }
        this.log = log
        if (publishers < 0) {
            throw new IllegalArgumentException("should not get publishers < 0")
        }
        this.publishers = publishers
        if (load == null) {
            throw new IllegalArgumentException("mapper should not be null")
        }
        this.load = load
        if (transform == null) {
            throw new IllegalArgumentException("stringTransform should not be null")
        }
        this.transform = transform
    }

    @Override
    Command<File> get() {
        final Supplier<StaticDispatchActor<T>> dynamo = new CommandBasedActorSupplier<T>(new DynamoCommandSupplier(log, load))
        final Supplier<StaticDispatchActor<T>> savers = new RandomlySelectingActorPool(publishers, dynamo)
        final Supplier<CloseableRepository<T>> repo = new BlackboardSupplier<T>(savers)
        final Supplier<Command<String>> transform = new StringTransformCommandSupplier<>(repo, log, transform)
        final Supplier<Command<File>> file = new FileCommandSupplier(new CommandBasedActorSupplier(transform))
        def supplier = new CommandBasedActorSupplier(file)
        new LoadFromFileViaActorsCommand(supplier)
    }
}
