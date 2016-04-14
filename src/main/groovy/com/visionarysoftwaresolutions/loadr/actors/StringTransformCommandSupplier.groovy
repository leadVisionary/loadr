package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.api.Command
import com.visionarysoftwaresolutions.loadr.dynamodb.DynamoDBPublisherSupplier

import groovy.transform.Immutable
import groovyx.gpars.actor.StaticDispatchActor
import org.slf4j.Logger

import java.util.function.Function
import java.util.function.Supplier

@Immutable
public final class StringTransformCommandSupplier<T, U> implements Supplier<Command<String>> {
    private final int publishers
    private final Logger log
    private final Function<T, U> mapper
    private final Function<String, T> stringTransform

    @Override
    Command<String> get() {
        final Supplier<StaticDispatchActor<T>> sup = new DynamoDBPublisherSupplier<T>(log, mapper)
        final Supplier<StaticDispatchActor<T>> savers = new RandomlySelectingActorPool(publishers, sup)
        new TransformStringAndSaveToRepositoryCommand(new Blackboard<>(savers), log, stringTransform)
    }
}
