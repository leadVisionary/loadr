package com.visionarysoftwaresolutions.loadr.api;

import java.util.function.Supplier;

public abstract class CommandSupplier<T> implements Supplier<Command<T>> {
    @Override
    public final Command<T> get() {
        return getCommand();
    }

    public abstract Command<T> getCommand();
}
