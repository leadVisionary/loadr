package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.api.CloseableRepository
import groovyx.gpars.actor.Actor
import groovyx.gpars.actor.StaticDispatchActor

import java.util.function.Supplier

final class Blackboard<T> implements CloseableRepository<T> {
    private final Set<T> stored
    private final Collection<StaticDispatchActor<T>> saverActors
    private final Supplier<StaticDispatchActor<T>> supplier

    Blackboard(final Supplier<StaticDispatchActor<T>> supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Should not get null supplier")
        }
        this.supplier = supplier
        stored = [] as Set<T>
        this.saverActors = []
    }

    @Override
    void save(T toSave) {
        stored << toSave
        sendToSubscriber(toSave)
    }

    private void sendToSubscriber(T toSave) {
        final StaticDispatchActor<T> publisher = supplier.get()
        publisher << toSave
        saverActors << publisher
    }

    @Override
    void close() throws Exception {
        saverActors.each { it << Actor.STOP_MESSAGE }
        stored.clear()
    }
}
