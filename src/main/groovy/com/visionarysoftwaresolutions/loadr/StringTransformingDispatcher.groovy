package com.visionarysoftwaresolutions.loadr

import groovyx.gpars.actor.Actor
import groovyx.gpars.actor.StaticDispatchActor

import java.time.Instant
import java.util.function.Function


final class StringTransformingDispatcher<T> extends StaticDispatchActor<String> {
    private final Collection<Actor> saverActors
    private final File log
    private final Function<String, T> transformer

    StringTransformingDispatcher( final Collection<Actor> saverActors,
                                  final File logFile,
                                  final Function<String, T> transformer ) {
        this.saverActors = saverActors.collect()
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
            saverActors[nextIndex] << transformer.apply(message)
        } catch (final Exception ex) {
            log.append(String.format("%s: Failed to write %s because %s %n%n", Instant.now(), message, ex))
        }
    }
}
