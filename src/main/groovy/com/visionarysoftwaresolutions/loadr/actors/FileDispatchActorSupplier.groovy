package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.api.Command
import com.visionarysoftwaresolutions.loadr.api.CommandBasedActorSupplier
import com.visionarysoftwaresolutions.loadr.api.CommandSupplier
import groovyx.gpars.actor.StaticDispatchActor

final class FileDispatchActorSupplier extends CommandBasedActorSupplier<File> {

    FileDispatchActorSupplier(CommandSupplier<File> supplier) {
        super(supplier)
    }

    @Override
    StaticDispatchActor<File> getActor(Command<File> command) {
        new CommandingActor<File>(command)
    }
}
