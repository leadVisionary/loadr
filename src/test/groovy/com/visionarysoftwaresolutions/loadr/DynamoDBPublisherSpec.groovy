package com.visionarysoftwaresolutions.loadr

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
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
}
