package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.api.Command
import groovy.transform.Immutable
import groovyx.gpars.actor.StaticDispatchActor

import java.util.function.Supplier

@Immutable
final class TransformingActorSupplier implements Supplier<StaticDispatchActor<String>> {
    private final Supplier<Command<String>> supplier

    @Override
    StaticDispatchActor<String> get() {
        Command<String> com = supplier.get()
        if (com == null) {
            throw new IllegalStateException("should not get null command")
        }
        final StaticDispatchActor<String> transformer = new PublishingActor(com)
        transformer.start()
        transformer
    }
}
