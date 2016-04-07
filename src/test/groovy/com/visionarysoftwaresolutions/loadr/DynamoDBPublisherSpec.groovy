package com.visionarysoftwaresolutions.loadr

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import jdk.nashorn.internal.ir.annotations.Immutable
import spock.lang.Shared

import java.util.function.Function

class DynamoDBPublisherSpec extends spock.lang.Specification {
    @Shared AmazonDynamoDB client = Mock(AmazonDynamoDB)
    @Shared File logFile = Mock(File)
    @Shared Function<String, PutItemRequest> transformer = Mock(Function)

    def "rejects null constructor arguments"() {
        when: "I try to construct with null client"
            new DynamoDBPublisher<String>(c, log, function)
        then: "I get an IllegalArgumentException"
            Exception e = thrown()
            e instanceof IllegalArgumentException
        where:
            c      | log       | function
            null   | logFile   | transformer
            client | null      | transformer
            client | logFile   | null
    }

    @Immutable
    class TestObject {
        String name
    }

    def "sends messages to client"() {
        given: "I have a publisher"
            DynamoDBPublisher<TestObject> publisher = new DynamoDBPublisher<TestObject>(client, logFile, transformer)
        and: "a message to be sent"
            TestObject message = new TestObject(name: "foo")
        and: "the actor is started"
            publisher.start()
        when: "I send a message to the publisher"
            publisher.sendAndWait(message)
        then: "the message is logged"
            1 * logFile.append(_)
        and: "the message is transformed"
            1 * transformer.apply(message)
        and: "the client has putItem called"
            1 * client.putItem(_)
    }
}
