package com.visionarysoftwaresolutions.loadr.dynamodb

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import com.amazonaws.services.dynamodbv2.model.PutItemResult

import java.util.function.Function

public final class LoadSingleIntoDynamo<T> implements Function<T, PutItemResult> {
    private final AmazonDynamoDB client
    private final Function<T, PutItemRequest> transformer

    public LoadSingleIntoDynamo(final AmazonDynamoDB client,
                                final  Function<T, PutItemRequest> transformer) {
        if (client == null) {
            throw new IllegalArgumentException("should not get null client")
        }
        this.client = client
        if (transformer == null) {
            throw new IllegalArgumentException("should not get null transformer")
        }
        this.transformer = transformer
    }

    @Override
    PutItemResult apply(T t) {
        client.putItem(transformer.apply(t))
    }
}
