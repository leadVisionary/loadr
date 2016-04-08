package com.visionarysoftwaresolutions.loadr

import groovyx.gpars.actor.Actor
import groovyx.gpars.actor.StaticDispatchActor
import org.slf4j.Logger

import java.time.Instant
import java.util.function.Function
import java.util.function.Supplier


final class StringTransformingDispatcher<T> extends StaticDispatchActor<String> {
    private final Collection<Actor> saverActors
    private final Logger log
    private final Function<String, T> transformer

    StringTransformingDispatcher(final Supplier<Collection<Actor>> supplier,
                                 final Logger logFile,
                                 final Function<String, T> transformer ) {
        this.saverActors = supplier.get()
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
            final int nextIndex = (int) (Math.random() * (saverActors.size() - 1)) + 1
            final Actor publisher = saverActors[nextIndex]
            if (!publisher.isActive()) {
                publisher.start()
            }
            publisher << transformer.apply(message)
        } catch (final Exception ex) {
            log.error(String.format("%s: Failed to write %s because %s %n%n", Instant.now(), message, ex))
        }
    }
}
