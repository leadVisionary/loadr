package com.visionarysoftwaresolutions.loadr

import java.util.function.Function
import java.util.stream.Stream

class StringTransformerSpec extends spock.lang.Specification {

    def "rejects null transformation function"() {
        when: "I try to create with null function"
            new StringTransformer(null)
        then: "An IllegalArgumentException is thrown"
            IllegalArgumentException e = thrown()
    }

    def "rejects null string stream"() {
        given: "a function that converts a string into something"
            Function<String, ?> func = Mock(Function)
        and: "A StringTransformer"
            StringTransformer<?> toTest = new StringTransformer<>(func)
        when: "I apply the function on nulls"
            toTest.apply(null)
        then: "An IllegalArgumentException is thrown"
            IllegalArgumentException e = thrown()
    }

    def "can transform a stream of strings into something else"() {
        given: "a function that converts a string into something"
            Function<String, ?> func = Mock(Function)
        and: "a Stream of Strings to be transformed"
            Stream<String> strings = Mock(Stream)
        and: "A StringTransformer"
            StringTransformer<?> toTest = new StringTransformer<>(func)
        when: "I apply the function"
            toTest.apply(strings)
        then: "the stream is mapped"
            1 * strings.map(func)
    }
}
