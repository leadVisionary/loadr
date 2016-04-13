package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.api.CloseableRepository
import groovyx.gpars.actor.StaticDispatchActor
import org.slf4j.Logger

import java.time.Instant
import java.util.function.Function


final class StringTransformingActor<T> extends StaticDispatchActor<String> {
    private final CloseableRepository<T> repository
    private final Logger log
    private final Function<String, T> transformer

    StringTransformingActor(final CloseableRepository<T> repository,
                            final Logger logFile,
                            final Function<String, T> transformer ) {
        if (repository == null) {
            throw new IllegalArgumentException("should not get null repository")
        }
        this.repository = repository
        if (logFile == null) {
            throw new IllegalArgumentException("should not get null log")
        }
        this.log = logFile
        if (transformer == null) {
            throw new IllegalArgumentException("should not get null transformer")
        }
        this.transformer = transformer
    }

    @Override
    void onMessage(final String message) {
        handleNextMessage(message)
    }

    private void handleNextMessage(final String message) {
        try {
            repository.save(transformer.apply(message))
        } catch (final Exception ex) {
            log.error(String.format("%s: Failed to write %s because %s %n%n", Instant.now(), message, ex))
        }
    }

    @Override
    protected void handleTermination() {
        super.handleTermination()
        repository.close()
    }
}
