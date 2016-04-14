package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.api.Command
import groovy.transform.Immutable
import groovyx.gpars.actor.StaticDispatchActor

import java.util.function.Supplier

@Immutable
class SubscriberSupplier<T> implements Supplier<StaticDispatchActor<T>> {
    private final int publishers
    private final Supplier<Command<T>> supplier

    @Override
    StaticDispatchActor<T> get() {
        final Supplier<StaticDispatchActor<T>> sup = new CommandBasedActorSupplier<T>(supplier)
        final Supplier<StaticDispatchActor<T>> savers = new RandomlySelectingActorPool(publishers, sup)
        savers
    }
}
