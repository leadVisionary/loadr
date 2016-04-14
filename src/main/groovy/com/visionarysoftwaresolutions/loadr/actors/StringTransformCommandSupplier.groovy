package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.api.CloseableRepository
import com.visionarysoftwaresolutions.loadr.api.Command

import groovy.transform.Immutable
import org.slf4j.Logger

import java.util.function.Function
import java.util.function.Supplier

@Immutable
public final class StringTransformCommandSupplier<T> implements Supplier<Command<String>> {
    private final Supplier<CloseableRepository<T>> supplier
    private final Logger log
    private final Function<String, T> stringTransform

    @Override
    Command<String> get() {
        CloseableRepository<T> repo = supplier.get()
        if (repo == null) {
            throw new IllegalStateException("should not get null repo")
        }
        new TransformStringAndSaveToRepositoryCommand(repo, log, stringTransform)
    }
}
