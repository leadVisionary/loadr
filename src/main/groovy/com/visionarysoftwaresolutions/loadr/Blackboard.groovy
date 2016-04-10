package com.visionarysoftwaresolutions.loadr

import com.visionarysoftwaresolutions.loadr.api.Repository
import groovyx.gpars.actor.Actor
import groovyx.gpars.actor.StaticDispatchActor

import java.util.function.Supplier

final class Blackboard<T> implements Repository<T> {
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

    void erase() {
        saverActors.each { it << Actor.STOP_MESSAGE }
        stored.clear()
    }
}
