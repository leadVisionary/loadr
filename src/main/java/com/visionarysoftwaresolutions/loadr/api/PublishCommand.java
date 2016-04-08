package com.visionarysoftwaresolutions.loadr.api;

public interface PublishCommand<T> {
    void execute(T toPublish);
}
