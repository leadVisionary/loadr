package com.visionarysoftwaresolutions.loadr.actors.extract

import com.visionarysoftwaresolutions.loadr.api.Command

import groovyx.gpars.actor.StaticDispatchActor

import java.util.function.Supplier

final class FileCommandSupplier implements Supplier<Command<File>> {
    private final Supplier<StaticDispatchActor<String>> supplier

    FileCommandSupplier(final Supplier<StaticDispatchActor<String>> supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("should not get null supplier")
        }
        this.supplier = supplier
    }

    @Override
    Command<File> get() {
        final StaticDispatchActor<String> transformer = supplier.get()
        if (transformer == null) {
            throw new IllegalStateException("should not get null actor")
        }
        new SendLinesOfFileToActorCommand(transformer)
    }
}
