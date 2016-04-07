package com.visionarysoftwaresolutions.loadr

import groovyx.gpars.actor.StaticDispatchActor
import groovyx.gpars.group.DefaultPGroup
import groovyx.gpars.scheduler.DefaultPool

import java.util.stream.Stream

import static java.nio.file.Files.lines

final class FileReader extends StaticDispatchActor<File> {
    private final StaticDispatchActor<String> transformer

    FileReader(final StaticDispatchActor<String> transformer) {
        this.transformer = transformer
        def readerPool = new DefaultPGroup(new DefaultPool(true, 1))
        this.parallelGroup = readerPool
    }

    @Override
    void onMessage(final File message) {
        final Stream<String> lines = lines(message.toPath())
        lines.forEach { String s -> transformer << s }
        transformer << "stop"
        stop()
    }
}
