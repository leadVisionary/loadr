package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.api.Command
import groovyx.gpars.actor.StaticDispatchActor
import groovyx.gpars.group.DefaultPGroup
import groovyx.gpars.group.PGroup
import groovyx.gpars.scheduler.DefaultPool

final class CommandingActor<T> extends StaticDispatchActor<T> {
    private final Command<T> command

    CommandingActor(final Command<T> command) {
        this(command,new DefaultPGroup(new DefaultPool(true, 1)))
    }

    protected CommandingActor(final Command<T> command,
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
    void onMessage(final T id) {
        command.execute(id)
    }
}
