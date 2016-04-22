package com.visionarysoftwaresolutions.loadr.dynamodb

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model.PutItemRequest

import java.util.function.Function

class LoadSingleIntoDynamoSpec extends spock.lang.Specification {

    def "rejects construction with nulls"() {
        when: "I construct an instance with nulls"
            new LoadSingleIntoDynamo(c, t)
        then: "An IllegalArgumentException is thrown"
            IllegalArgumentException e = thrown()
        where: c                    | t
               null                 | Mock(Function)
               Mock(AmazonDynamoDB) | null
    }

    def "applies transform and puts an item"() {
        given: "an AmazonDynamoDB client"
            AmazonDynamoDB client = Mock()
        and: "a transformation function"
            Function<String, PutItemRequest> transform = Mock()
        and: "A LoadSingleIntoDynamo"
            LoadSingleIntoDynamo<String> toTest = new LoadSingleIntoDynamo<>(client, transform)
        and: "a param to apply"
            def param = "foo"
        when: "I call the function with a param"
            toTest.apply(param)
        then: "A transformation is applied"
            1 * transform.apply(param)
        and: "the client is invoked to put an item"
            1 * client.putItem(_)
    }
}
