package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.api.Command
import groovyx.gpars.actor.StaticDispatchActor

final class StringTransformingActor<T> extends StaticDispatchActor<String> {
    private final Command<String> command

    StringTransformingActor(final Command<String> command) {
        if (command == null) {
            throw new IllegalArgumentException("should not get null command")
        }
        this.command = command
    }

    @Override
    void onMessage(final String message) {
        command.execute(message)
    }
}
