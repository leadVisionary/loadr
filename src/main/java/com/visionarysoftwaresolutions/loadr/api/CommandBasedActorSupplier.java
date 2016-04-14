package com.visionarysoftwaresolutions.loadr.api;

import groovyx.gpars.actor.StaticDispatchActor;

import java.util.function.Supplier;

public abstract class CommandBasedActorSupplier<T> implements Supplier<StaticDispatchActor<T>> {
    protected CommandBasedActorSupplier(final CommandSupplier<T> supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Should not get null supplier");
        }

        this.supplier = supplier;
    }

    @Override
    public final StaticDispatchActor<T> get() {
        final Command<T> transformer = supplier.get();
        if (transformer == null) {
            throw new IllegalStateException("should not get null Command");
        }

        return getActor(transformer);
    }

    public abstract StaticDispatchActor<T> getActor(Command<T> command);

    private final CommandSupplier<T> supplier;
}
