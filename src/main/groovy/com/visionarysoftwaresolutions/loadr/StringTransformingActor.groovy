package com.visionarysoftwaresolutions.loadr

import groovyx.gpars.actor.StaticDispatchActor
import org.slf4j.Logger

import java.time.Instant
import java.util.function.Function


final class StringTransformingActor<T> extends StaticDispatchActor<String> {
    private final Blackboard<T> repository
    private final Logger log
    private final Function<String, T> transformer

    StringTransformingActor(final Blackboard<T> repository,
                            final Logger logFile,
                            final Function<String, T> transformer ) {
        this.repository = repository
        this.log = logFile
        this.transformer = transformer
    }

    @Override
    void onMessage(final String message) {
        if (message == "stop") {
            repository.erase()
            stop()
        }
        else {
            handleNextMessage(message)
        }
    }

    private void handleNextMessage(final String message) {
        try {
            repository.save(transformer.apply(message))
        } catch (final Exception ex) {
            log.error(String.format("%s: Failed to write %s because %s %n%n", Instant.now(), message, ex))
        }
    }
}
