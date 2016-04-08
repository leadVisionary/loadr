package com.visionarysoftwaresolutions.loadr

import groovyx.gpars.actor.StaticDispatchActor
import groovyx.gpars.group.DefaultPGroup
import groovyx.gpars.scheduler.FJPool

import java.util.function.Supplier

final class RandomlySelectingActorPool<T> implements Supplier<StaticDispatchActor<T>> {
    private final Collection<StaticDispatchActor<T>> actors

    RandomlySelectingActorPool(final int numPublishers,
                               final Supplier<StaticDispatchActor<T>> supplier) {
        def publisherPool = new DefaultPGroup(new FJPool(numPublishers))
        actors = []
        numPublishers.times {
            final StaticDispatchActor<T> publisher = supplier.get()
            publisher.parallelGroup = publisherPool
            actors << publisher
        }
    }


    @Override
    StaticDispatchActor<T> get() {
        final int nextIndex = (int) (Math.random() * (actors.size() - 1)) + 1
        final StaticDispatchActor<T> publisher = actors[nextIndex]
        if (!publisher.isActive()) {
            publisher.start()
        }
        publisher
    }
}
