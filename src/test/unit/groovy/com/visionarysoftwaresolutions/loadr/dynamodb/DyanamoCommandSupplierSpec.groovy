package com.visionarysoftwaresolutions.loadr.dynamodb

import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import com.visionarysoftwaresolutions.loadr.api.Command
import org.slf4j.Logger

import java.util.function.Function

class DyanamoCommandSupplierSpec extends spock.lang.Specification {
    def "rejects null inputs"() {
        when: "I try to create with null"
            new DynamoCommandSupplier(log, func)
        then: "An IllegalArgumentException is thrown"
            IllegalArgumentException e = thrown()
        where: log          | func
               null         | Mock(Function)
               Mock(Logger) | null
    }

    def "can get a DynamoDBCommand"() {
        given: "a Logger"
            Logger log = Mock(Logger)
        and: "a function that transforms into PutItemRequest"
            Function<?, PutItemRequest> func = Mock(Function)
        when: "I try to create with null"
            DynamoCommandSupplier<?> toTest = new DynamoCommandSupplier(log, func)
        and: "I ask for a command"
            Command<?> com = toTest.get()
        then: "the Command is a DynamoDBCommand"
            com instanceof DynamoDBCommand

    }
}
