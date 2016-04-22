package com.visionarysoftwaresolutions.loadr

import java.util.function.Function
import java.util.stream.Stream

public final class StringTransformer<T> implements Function<Stream<String>, Stream<T>> {
    private final Function<String, T> transform

    public StringTransformer(final Function<String, T> func) {
        transform = func
    }

    @Override
    Stream<T> apply(Stream<String> s) {
        s.map {
            transform.apply(it)
        }
    }
}
