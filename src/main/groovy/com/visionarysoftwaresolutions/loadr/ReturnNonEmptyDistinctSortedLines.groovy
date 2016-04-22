package com.visionarysoftwaresolutions.loadr

import java.nio.file.Files
import java.util.function.Function
import java.util.stream.Stream

public final class ReturnNonEmptyDistinctSortedLines implements Function<File, Stream<String>> {

    @Override
    Stream<String> apply(final File file) {
        if (file == null) {
            throw new IllegalArgumentException("Should not get null file")
        }
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("should only get a file with lines")
        }
        final Stream<String> lines = Files.lines(file.toPath())
        lines.parallel().filter {
            !(it.isEmpty() || it.isAllWhitespace())
        }.distinct().sorted()
    }
}
