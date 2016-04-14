package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.dynamodb.DynamoDBPublisherSupplier
import groovy.transform.Immutable
import groovyx.gpars.actor.StaticDispatchActor
import org.slf4j.Logger

import java.util.function.Function
import java.util.function.Supplier

@Immutable
class SubscriberSupplier<T, U> implements Supplier<StaticDispatchActor<T>> {
    private final int publishers
    private final Logger log
    private final Function<T, U> mapper

    @Override
    StaticDispatchActor<T> get() {
        final Supplier<StaticDispatchActor<T>> sup = new DynamoDBPublisherSupplier<T>(new DynamoCommandSupplier<T>(log, mapper))
        final Supplier<StaticDispatchActor<T>> savers = new RandomlySelectingActorPool(publishers, sup)
        savers
    }
}
