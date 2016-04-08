package com.visionarysoftwaresolutions.loadr

import groovyx.gpars.actor.Actor
import groovyx.gpars.actor.StaticDispatchActor
import org.slf4j.Logger

import java.time.Instant
import java.util.function.Function
import java.util.function.Supplier


final class StringTransformingActor<T> extends StaticDispatchActor<String> {
    private final Collection<StaticDispatchActor<T>> saverActors
    private final Supplier<StaticDispatchActor<T>> supplier
    private final Logger log
    private final Function<String, T> transformer

    StringTransformingActor(final Supplier<StaticDispatchActor<T>> supplier,
                            final Logger logFile,
                            final Function<String, T> transformer ) {
        this.saverActors = []
        this.supplier = supplier
        this.log = logFile
        this.transformer = transformer
    }

    @Override
    void onMessage(final String message) {
        if (message == "stop") {
            saverActors.each { it << Actor.STOP_MESSAGE }
            stop()
        }
        else {
            handleNextMessage(message)
        }
    }

    private void handleNextMessage(final String message) {
        try {
            final StaticDispatchActor<T> publisher = supplier.get()
            publisher << transformer.apply(message)
            saverActors << publisher
        } catch (final Exception ex) {
            log.error(String.format("%s: Failed to write %s because %s %n%n", Instant.now(), message, ex))
        }
    }
}
