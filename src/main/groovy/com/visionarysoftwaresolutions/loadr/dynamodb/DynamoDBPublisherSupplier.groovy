package com.visionarysoftwaresolutions.loadr.dynamodb

import com.visionarysoftwaresolutions.loadr.actors.PublishingActor
import com.visionarysoftwaresolutions.loadr.api.Command
import groovyx.gpars.actor.StaticDispatchActor
import java.util.function.Supplier

final class DynamoDBPublisherSupplier<T> implements Supplier<StaticDispatchActor<T>> {
    private final Supplier<Command<T>> supplier

    DynamoDBPublisherSupplier(final Supplier<Command<T>> supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("should not get null supplier")
        }
        this.supplier = supplier
    }

    @Override
    StaticDispatchActor<T> get() {
        def command = supplier.get()
        if (command == null) {
            throw new IllegalStateException("Should not get null Command")
        }
        new PublishingActor(command)
    }
}
