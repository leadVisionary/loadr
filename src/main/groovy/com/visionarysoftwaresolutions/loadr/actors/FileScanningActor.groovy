package com.visionarysoftwaresolutions.loadr.actors

import groovyx.gpars.actor.StaticDispatchActor
import groovyx.gpars.group.DefaultPGroup
import groovyx.gpars.scheduler.DefaultPool

import java.util.stream.Stream

import static java.nio.file.Files.lines

final class FileScanningActor extends StaticDispatchActor<File> {
    private final StaticDispatchActor<String> transformer

    FileScanningActor(final StaticDispatchActor<String> transformer) {
        if (transformer == null) {
            throw new IllegalArgumentException("should not get null transformer")
        }
        this.transformer = transformer
        def readerPool = new DefaultPGroup(new DefaultPool(true, 1))
        this.parallelGroup = readerPool
    }

    @Override
    void onMessage(final File message) {
        final Stream<String> lines = lines(message.toPath())
        lines.forEach { String s -> transformer << s }
        transformer.stop()
        stop()
    }
}
