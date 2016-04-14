package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.api.Command
import com.visionarysoftwaresolutions.loadr.api.CommandSupplier
import groovy.transform.Immutable
import groovyx.gpars.actor.StaticDispatchActor

import java.util.function.Supplier

@Immutable
class FileCommandSupplier extends CommandSupplier<File> {
    private final Supplier<StaticDispatchActor<String>> supplier

    @Override
    Command<File> getCommand() {
        final StaticDispatchActor<String> transformer = supplier.get()
        if (transformer == null) {
            throw new IllegalStateException("should not get null actor")
        }
        new SendLinesOfFileToActorCommand(transformer)
    }
}
