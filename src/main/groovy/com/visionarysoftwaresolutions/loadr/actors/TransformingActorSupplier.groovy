package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.api.Command
import groovyx.gpars.actor.StaticDispatchActor

import java.util.function.Supplier

final class TransformingActorSupplier implements Supplier<StaticDispatchActor<String>> {
    private final Supplier<Command<String>> supplier

    @Override
    StaticDispatchActor<String> get() {
        Command<String> com = supplier.get()
        if (com == null) {
            throw new IllegalStateException("should not get null command")
        }
        final StaticDispatchActor<String> transformer = new StringTransformingActor(com)
        transformer.start()
        transformer
    }
}
