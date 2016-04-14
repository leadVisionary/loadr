package com.visionarysoftwaresolutions.loadr.streams

import org.slf4j.Logger
import streams.StreamBasedLoadFromFileCommand

import java.util.function.Function

class StreamBasedLoadFromFileCommandSpec extends spock.lang.Specification {

    def "rejects null inputs and negative publishers"() {
        when: "constructed with null references or negative publishers"
            new StreamBasedLoadFromFileCommand(log, p, e, t, l)
        then: "An IllegalArgumentException is thrown"
            IllegalArgumentException exception = thrown()
        where: log          | p  | e              | t              | l
               null         | 1  | Mock(Function) | Mock(Function) | Mock(Function)
               Mock(Logger) | -1 | Mock(Function) | Mock(Function) | Mock(Function)
               Mock(Logger) | 1  | null           | Mock(Function) | Mock(Function)
               Mock(Logger) | 1  | Mock(Function) | null           | Mock(Function)
               Mock(Logger) | 1  | Mock(Function) | Mock(Function) | null

    }

    def "can execute an ETL job"() {
        given: "A Logger"
        and: "A desired parallelism"
        and: "An Extract Function"
        and: "A Transform Function"
        and: "A Load Function"
        and: "A StreamBasedLoadFromFileCommand to test"
        and: "A File to be processed"
        when: "I execute the Command by passing the file"
        then: "each line of the file has the extract function applied"
        and: "the result of extract is passed to transform"
        and: "the result of transform is passed to load"
    }
}
