package com.visionarysoftwaresolutions.loadr.actors

import groovyx.gpars.actor.StaticDispatchActor

import java.util.function.Function

public final class Agent<T, R> extends StaticDispatchActor<T> {
    private final Function<T, R> function

    public Agent(final Function<T, R> toApply) {
        if (toApply == null) {
            throw new IllegalArgumentException("Should not get null function")
        }
        function = toApply
    }

    @Override
    void onMessage(final T message) {
        def r = function.apply(message)
        replyIfExists(r)
    }
}
