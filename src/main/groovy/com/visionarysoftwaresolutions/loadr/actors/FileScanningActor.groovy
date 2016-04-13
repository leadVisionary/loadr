package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.api.Command
import groovyx.gpars.actor.StaticDispatchActor
import groovyx.gpars.group.DefaultPGroup
import groovyx.gpars.scheduler.DefaultPool

final class FileScanningActor extends StaticDispatchActor<File> {
    private final Command<File> command

    FileScanningActor(final Command<File> command) {
        if (command == null) {
            throw new IllegalArgumentException("should not get null transformer")
        }
        this.command = command
        def readerPool = new DefaultPGroup(new DefaultPool(true, 1))
        this.parallelGroup = readerPool
    }

    @Override
    void onMessage(final File message) {
        command.execute(message)
        stop()
    }
}
