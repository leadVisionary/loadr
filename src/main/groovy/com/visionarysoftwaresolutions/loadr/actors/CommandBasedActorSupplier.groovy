package com.visionarysoftwaresolutions.loadr.actors

import groovy.transform.Immutable
import groovyx.gpars.actor.StaticDispatchActor

import java.util.function.Supplier

@Immutable
public final class CommandBasedActorSupplier implements Supplier<StaticDispatchActor<File>> {
    private final Supplier<StaticDispatchActor<String>> supplier

    @Override
    StaticDispatchActor<File> get() {
        final StaticDispatchActor<String> transformer = supplier.get()
        return new FileScanningActor(new SendLinesOfFileToActorCommand(transformer))
    }
}
