package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.api.Command
import groovyx.gpars.actor.StaticDispatchActor

import java.util.function.Supplier
import java.util.stream.Stream

public final class LoadFromFileViaActorsCommand<T> implements Command<File> {
    private final Supplier<StaticDispatchActor<File>> extractor
    private final Supplier<StaticDispatchActor<Stream<String>>> transformer
    private final Supplier<StaticDispatchActor<Stream<T>>> loader

    LoadFromFileViaActorsCommand(final Supplier<StaticDispatchActor<File>> filer,
            final Supplier<StaticDispatchActor<Stream<String>>> optimusPrime,
            final Supplier<StaticDispatchActor<Stream<T>>> drunk) {
        if (filer == null) {
            throw new IllegalArgumentException("should not get null filer")
        }
        this.extractor = filer
        if (optimusPrime == null) {
            throw new IllegalArgumentException("should not get null transformer")
        }
        this.transformer = optimusPrime
        if (drunk == null) {
            throw new IllegalArgumentException("should not get null loader")
        }
        this.loader = drunk
    }


    @Override
    void execute(final File parameter) {
        if (parameter == null || !parameter.isFile()) {
            throw new IllegalArgumentException("Should get valid file")
        }

        def actors = [extractor, transformer, loader].collect {
            def actor = it.get()
            if (actor == null ) {
                throw new IllegalStateException("Supplier returned null")
            }
            if (!actor.isActive()) {
                actor.start()
            }
            actor
        }

        def read = actors[0].sendAndPromise(parameter)
        def transformed = actors[1].sendAndPromise(read.get())
        actors[2].sendAndPromise(transformed.get())
        actors*.stop()
        actors*.join()
    }
}
