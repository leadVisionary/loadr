package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.api.Command
import groovyx.gpars.actor.StaticDispatchActor
import groovyx.gpars.group.DefaultPGroup
import groovyx.gpars.group.PGroup
import groovyx.gpars.scheduler.DefaultPool

final class FileScanningActor extends StaticDispatchActor<File> {
    private final Command<File> command

    public FileScanningActor(final Command<File> command) {
        this(command,new DefaultPGroup(new DefaultPool(true, 1)))
    }

    protected FileScanningActor(final Command<File> command,
                                final PGroup group) {
        if (command == null) {
            throw new IllegalArgumentException("should not get null command")
        }
        this.command = command
        if (group == null) {
            throw new IllegalArgumentException("should not get null group")
        }
        this.parallelGroup = group
    }

    @Override
    void onMessage(final File message) {
        command.execute(message)
    }
}
