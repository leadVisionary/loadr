package com.visionarysoftwaresolutions.loadr

import groovyx.gpars.actor.StaticDispatchActor

final class DynamoDBPublisher<T> extends StaticDispatchActor<T> {
    private final PublishCommand<T> command
    DynamoDBPublisher(final PublishCommand<T> command) {
        if (command == null) {
            throw new IllegalArgumentException("Should not get null command")
        }
    }

    @Override
    void onMessage(final T id) {
        command.execute(id)
    }
}
