package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.api.Command
import groovyx.gpars.actor.StaticDispatchActor

import java.util.stream.Stream

import static java.nio.file.Files.lines

final class SendLinesOfFileToActorCommand implements Command<File> {
    private final StaticDispatchActor<String> transformer

    SendLinesOfFileToActorCommand(final StaticDispatchActor<String> transformer) {
        if (transformer == null) {
            throw new IllegalArgumentException("should not get null transformer")
        }
        this.transformer = transformer
    }

    @Override
    void execute(File parameter) {
        if (!transformer.isActive()) {
            transformer.start()
        }
        final Stream<String> lines = lines(parameter.toPath())
        lines.forEach { String s -> transformer << s }
        if (transformer.isActive()) {
            transformer.stop()
        }
    }
}
