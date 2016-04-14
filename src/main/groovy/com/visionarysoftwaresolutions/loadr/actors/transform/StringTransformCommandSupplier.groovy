package com.visionarysoftwaresolutions.loadr.actors.transform

import com.visionarysoftwaresolutions.loadr.api.CloseableRepository
import com.visionarysoftwaresolutions.loadr.api.Command

import org.slf4j.Logger

import java.util.function.Function
import java.util.function.Supplier


public final class StringTransformCommandSupplier<T> implements Supplier<Command<String>> {
    private final Supplier<CloseableRepository<T>> supplier
    private final Logger log
    private final Function<String, T> stringTransform

    StringTransformCommandSupplier(final Supplier<CloseableRepository<T>> supplier,
                                   final Logger log,
                                   final Function<String, T> stringTransform) {
        if (supplier == null) {
            throw new IllegalArgumentException("should not get null supplier")
        }
        this.supplier = supplier
        if (log == null) {
            throw new IllegalArgumentException("should not get null log")
        }
        this.log = log
        if (stringTransform == null) {
            throw new IllegalArgumentException("should not get null stringTransform")
        }
        this.stringTransform = stringTransform
    }

    @Override
    Command<String> get() {
        CloseableRepository<T> repo = supplier.get()
        if (repo == null) {
            throw new IllegalStateException("should not get null repo")
        }
        new TransformStringAndSaveToRepositoryCommand(repo, log, stringTransform)
    }
}
