package com.visionarysoftwaresolutions.loadr.actors

import groovyx.gpars.actor.StaticDispatchActor

import java.util.function.Function

public final class Agent<T, R> extends StaticDispatchActor<T> {
    private final Function<T, R> function

    public Agent(final Function<T, R> toApply) {
        function = toApply
    }

    @Override
    void onMessage(final T message) {
        def r = function.apply(message)
        reply(r)
    }
}
