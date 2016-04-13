package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.api.CloseableRepository
import com.visionarysoftwaresolutions.loadr.dynamodb.DynamoDBPublisherSupplier
import groovy.transform.Immutable
import groovyx.gpars.actor.StaticDispatchActor
import org.slf4j.Logger

import java.util.function.Function
import java.util.function.Supplier

@Immutable
public final class CommandBasedActorSupplier<T, U> implements Supplier<StaticDispatchActor<File>> {
    private final int publishers
    private final Logger log
    private final Function<T, U> mapper
    private final Function<String, T> stringTransform

    @Override
    FileScanningActor get() {
        CloseableRepository<T> repo = getSync()
        final TransformStringAndSaveToRepositoryCommand com = new TransformStringAndSaveToRepositoryCommand(repo, log, stringTransform)
        final StaticDispatchActor<String> transformer = new StringTransformingActor(com)
        transformer.start()
        return new FileScanningActor(new SendLinesOfFileToActorCommand(transformer))
    }

    private CloseableRepository<T> getSync() {
        final Supplier<StaticDispatchActor<T>> sup = new DynamoDBPublisherSupplier<T>(log, mapper)
        final Supplier<StaticDispatchActor<T>> savers = new RandomlySelectingActorPool(publishers, sup)
        new Blackboard<>(savers)
    }
}
