package com.visionarysoftwaresolutions.loadr

import java.util.function.Function
import java.util.stream.Stream

public final class StringTransformer<T> implements Function<Stream<String>, Stream<T>> {
    private final Function<String, T> transform

    public StringTransformer(final Function<String, T> func) {
        if (func == null) {
            throw new IllegalArgumentException("should not get null func")
        }
        transform = func
    }

    @Override
    Stream<T> apply(Stream<String> s) {
        if (s == null) {
            throw new IllegalArgumentException("should not get null stream")
        }
        s.map transform
    }
}
