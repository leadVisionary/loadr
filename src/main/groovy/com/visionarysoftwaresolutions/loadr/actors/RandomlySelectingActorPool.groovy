package com.visionarysoftwaresolutions.loadr.actors

import groovyx.gpars.actor.StaticDispatchActor
import groovyx.gpars.group.DefaultPGroup
import groovyx.gpars.scheduler.FJPool

import java.util.function.Supplier

final class RandomlySelectingActorPool<T> implements Supplier<StaticDispatchActor<T>> {
    private static final Random RANDOMIZER = new Random()
    private final Collection<StaticDispatchActor<T>> actors

    RandomlySelectingActorPool(final int numPublishers,
                               final Supplier<StaticDispatchActor<T>> supplier) {
        if (numPublishers < 0) {
            throw new IllegalArgumentException("numPublishers should be > 0")
        }

        if(supplier == null) {
            throw new IllegalArgumentException("should not get null supplier")
        }

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
        final int nextIndex = RANDOMIZER.nextInt(actors.size())
        final StaticDispatchActor<T> publisher = actors[nextIndex]
        if (!publisher.isActive()) {
            publisher.start()
        }
        publisher
    }
}
