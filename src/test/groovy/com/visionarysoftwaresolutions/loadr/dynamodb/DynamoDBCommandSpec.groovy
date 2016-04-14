package com.visionarysoftwaresolutions.loadr.dynamodb

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputExceededException
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import org.slf4j.Logger

import java.util.function.Function

class DynamoDBCommandSpec extends spock.lang.Specification {

    def "rejects null constructor arguments"() {
        when: "I try to construct with null client"
            new DynamoDBCommand<String>(c, log, function)
        then: "I get an IllegalArgumentException"
            Exception e = thrown()
            e instanceof IllegalArgumentException
        where:
            c                    | log            | function
            null                 | Mock(Logger)   | Mock(Function)
            Mock(AmazonDynamoDB) | null           | Mock(Function)
            Mock(AmazonDynamoDB) | Mock(Logger)   | null
    }

    def "sends messages to client"() {
        given: "a dynamo db client"
            AmazonDynamoDB client = Mock(AmazonDynamoDB)
        and: "a logger"
            Logger logFile = Mock(Logger)
        and: "a transform function"
            Function<String, PutItemRequest> transformer = Mock(Function)
        and: "I have a publisher"
            DynamoDBCommand<String> command = new DynamoDBCommand<>(client, logFile, transformer)
        and: "a message to be sent"
            String message ="foo"
        when: "I try tp publish"
            command.execute(message)
        then: "the message is logged"
            1 * logFile.debug(_)
        and: "the transformation is applied"
            1 * transformer.apply(message)
        and: "the item is put"
            1 * client.putItem(_)
    }

    def "logs errors"() {
        given: "a dynamo db client"
            AmazonDynamoDB client = Mock(AmazonDynamoDB)
        and: "a logger"
            Logger logFile = Mock(Logger)
        and: "a transform function"
            Function<String, PutItemRequest> transformer = Mock(Function)
        and: "I have a publisher"
            DynamoDBCommand<String> command = new DynamoDBCommand<>(client, logFile, transformer)
        and: "a message to be sent"
            String message ="foo"
        when: "I try tp publish"
            command.execute(message)
        then: "the message is logged"
            1 * logFile.debug(_)
        and: "the transformation is applied"
            1 * transformer.apply(message)
        and: "the item is put"
            1 * client.putItem(_) >> new ProvisionedThroughputExceededException("whoops")
        and: "the exception is logged"
            1 * logFile.error(_)
    }
}
