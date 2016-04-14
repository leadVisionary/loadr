package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.api.Command
import groovy.transform.Immutable
import groovyx.gpars.actor.StaticDispatchActor

import java.util.function.Supplier

@Immutable
public final class CommandBasedActorSupplier implements Supplier<StaticDispatchActor<File>> {
    private final CommandSupplier<File> supplier

    @Override
    StaticDispatchActor<File> get() {
        final Command<File> transformer = supplier.get()
        if (transformer == null) {
            throw new IllegalStateException("should not get null Command")
        }
        return new FileScanningActor(transformer)
    }
}
