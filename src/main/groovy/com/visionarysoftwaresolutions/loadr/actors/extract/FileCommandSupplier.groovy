package com.visionarysoftwaresolutions.loadr.actors.extract

import com.visionarysoftwaresolutions.loadr.api.Command
import groovy.transform.Immutable
import groovyx.gpars.actor.StaticDispatchActor

import java.util.function.Supplier

@Immutable
class FileCommandSupplier implements Supplier<Command<File>> {
    private final Supplier<StaticDispatchActor<String>> supplier

    @Override
    Command<File> get() {
        final StaticDispatchActor<String> transformer = supplier.get()
        if (transformer == null) {
            throw new IllegalStateException("should not get null actor")
        }
        new SendLinesOfFileToActorCommand(transformer)
    }
}
