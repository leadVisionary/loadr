package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.api.PublishCommand
import groovyx.gpars.actor.StaticDispatchActor

final class PublishingActor<T> extends StaticDispatchActor<T> {
    private final PublishCommand<T> command

    PublishingActor(final PublishCommand<T> command) {
        if (command == null) {
            throw new IllegalArgumentException("Should not get null command")
        }
        this.command = command
    }

    @Override
    void onMessage(final T id) {
        command.execute(id)
    }
}
