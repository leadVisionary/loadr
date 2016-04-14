package com.visionarysoftwaresolutions.loadr.actors.store

import com.visionarysoftwaresolutions.loadr.api.CloseableRepository
import groovyx.gpars.actor.StaticDispatchActor
import java.util.function.Supplier

final class BlackboardSupplier<T> implements Supplier<CloseableRepository<T>> {
    private final Supplier<StaticDispatchActor<T>> supplier

    BlackboardSupplier(final Supplier<StaticDispatchActor<T>> supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("should not get null supplier")
        }
        this.supplier = supplier
    }

    @Override
    CloseableRepository<T> get() {
        new Blackboard<>(supplier)
    }
}
