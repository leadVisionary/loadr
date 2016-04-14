package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.api.CloseableRepository
import groovy.transform.Immutable
import groovyx.gpars.actor.StaticDispatchActor
import java.util.function.Supplier

@Immutable
class BlackboardSupplier<T> implements Supplier<CloseableRepository<T>> {
    private final Supplier<StaticDispatchActor<T>> supplier

    @Override
    CloseableRepository<T> get() {
        new Blackboard<>(supplier)
    }
}
