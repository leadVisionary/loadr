package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.api.Command

import java.util.function.Supplier

abstract class CommandSupplier<T> implements Supplier<Command<T>> {
    @Override
    Command<T> get() {
        getCommand()
    }

    abstract Command<T> getCommand()
}
