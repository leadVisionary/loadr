package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.api.Command
import groovyx.gpars.actor.StaticDispatchActor

import java.util.function.Supplier

final class LoadFromFileViaActorsCommand implements Command<File> {
    private final Supplier<StaticDispatchActor<File>> supplier

    LoadFromFileViaActorsCommand(final Supplier<StaticDispatchActor<File>> supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("should not get null supplier")
        }
        this.supplier = supplier
    }


    @Override
    void execute(final File parameter) {
        if (parameter == null || !parameter.isFile()) {
            throw new IllegalArgumentException("Should get valid file")
        }
        final StaticDispatchActor<File> reader = supplier.get()
        if( reader == null ) {
            throw new IllegalStateException("Supplier returned null")
        }

        if(!reader.active) {
            reader.start()
        }
        reader << parameter
        reader.join()
    }
}
