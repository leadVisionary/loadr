package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.api.CloseableRepository
import com.visionarysoftwaresolutions.loadr.api.Command
import org.slf4j.Logger

import java.time.Instant
import java.util.function.Function

final class TransformStringAndSaveToRepositoryCommand<T> implements Command<String> {
    private final CloseableRepository<T> repository
    private final Logger log
    private final Function<String, T> transformer

    TransformStringAndSaveToRepositoryCommand(final CloseableRepository<T> repository,
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
    void execute(String parameter) {
        try {
            repository.save(transformer.apply(parameter))
        } catch (final Exception ex) {
            log.error(String.format("%s: Failed to write %s because %s %n%n", Instant.now(), parameter, ex))
        }
    }
}
