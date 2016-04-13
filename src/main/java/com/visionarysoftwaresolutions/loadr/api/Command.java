package com.visionarysoftwaresolutions.loadr.api;

public interface Command<T> {
    void execute(T parameter);
}
