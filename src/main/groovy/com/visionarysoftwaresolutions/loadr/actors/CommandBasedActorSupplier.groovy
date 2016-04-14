package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.api.Command
import groovyx.gpars.actor.StaticDispatchActor

import java.util.function.Supplier

public abstract class CommandBasedActorSupplier<T> implements Supplier<StaticDispatchActor<T>> {
    private final CommandSupplier<T> supplier

    protected CommandBasedActorSupplier(final CommandSupplier<T> supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Should not get null supplier")
        }
        this.supplier = supplier
    }

    @Override
    StaticDispatchActor<T> get() {
        final Command<T> transformer = supplier.get()
        if (transformer == null) {
            throw new IllegalStateException("should not get null Command")
        }
        return getActor(transformer)
    }

    abstract StaticDispatchActor<T> getActor(Command<T> transformer)
}
