package com.visionarysoftwaresolutions.loadr.dynamodb

import com.visionarysoftwaresolutions.loadr.api.CommandBasedActorSupplier
import com.visionarysoftwaresolutions.loadr.api.CommandSupplier
import com.visionarysoftwaresolutions.loadr.actors.CommandingActor
import com.visionarysoftwaresolutions.loadr.api.Command
import groovyx.gpars.actor.StaticDispatchActor

final class DynamoDBPublisherSupplier<T> extends CommandBasedActorSupplier<T> {

    protected DynamoDBPublisherSupplier(final CommandSupplier<T> supplier) {
        super(supplier)
    }

    @Override
    StaticDispatchActor<T> getActor(Command<T> command) {
        new CommandingActor(command)
    }
}
