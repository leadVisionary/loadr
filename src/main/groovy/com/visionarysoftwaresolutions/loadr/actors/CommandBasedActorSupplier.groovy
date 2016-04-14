package com.visionarysoftwaresolutions.loadr.actors;

import com.visionarysoftwaresolutions.loadr.api.Command;
import groovyx.gpars.actor.StaticDispatchActor;

import java.util.function.Supplier;

final class CommandBasedActorSupplier<T> implements Supplier<StaticDispatchActor<T>> {

    private final Supplier<Command<T>> supplier;

    protected CommandBasedActorSupplier(final Supplier<Command<T>> supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Should not get null supplier");
        }

        this.supplier = supplier;
    }

    @Override
    public final StaticDispatchActor<T> get() {
        final Command<T> command = supplier.get();
        if (command == null) {
            throw new IllegalStateException("should not get null Command");
        }

        final StaticDispatchActor<String> actor = new CommandingActor(command)
        actor.start()
        actor
    }
}
