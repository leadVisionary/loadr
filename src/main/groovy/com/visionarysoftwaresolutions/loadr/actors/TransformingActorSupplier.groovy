package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.api.Command
import com.visionarysoftwaresolutions.loadr.api.CommandBasedActorSupplier
import com.visionarysoftwaresolutions.loadr.api.CommandSupplier
import groovyx.gpars.actor.StaticDispatchActor

final class TransformingActorSupplier extends CommandBasedActorSupplier<String> {

    protected TransformingActorSupplier(
            CommandSupplier<String> supplier) {
        super(supplier)
    }

    @Override
    StaticDispatchActor<String> getActor(Command<String> command) {
        final StaticDispatchActor<String> transformer = new CommandingActor(com)
        transformer.start()
        transformer
    }
}
