package com.visionarysoftwaresolutions.loadr;

public interface PublishCommand<T> {
    void execute(T toPublish);
}
