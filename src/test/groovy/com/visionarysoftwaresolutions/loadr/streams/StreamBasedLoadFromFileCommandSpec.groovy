package com.visionarysoftwaresolutions.loadr.streams

import org.slf4j.Logger
import streams.StreamBasedLoadFromFileCommand

import java.nio.file.Files
import java.nio.file.Paths
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
            Logger log = Mock(Logger)
        and: "A desired parallelism"
            int publishers = 1
        and: "An Extract Function"
            Function<String, String> e = Mock(Function)
        and: "A Transform Function"
            Function<String, Integer> t = Mock(Function)
        and: "A Load Function"
            Function<Integer, String> l = Mock(Function)
        and: "A StreamBasedLoadFromFileCommand to test"
            StreamBasedLoadFromFileCommand<String, Integer> toTest = new StreamBasedLoadFromFileCommand<>(log, publishers, e,t,l)
        and: "A File to be processed"
            File temp = Files.createTempFile(Paths.get(System.getProperty("user.dir")), "foo", "bar").toFile()
            temp.write("1 cool dudebro\n2 lame brodawg")
            temp.deleteOnExit()
        when: "I execute the Command by passing the file"
            toTest.execute(temp)
        then: "each line of the file has the extract function applied"
            2 * e.apply(_ as String) >> { "foo" }
        and: "the result of extract is passed to transform"
            2 * t.apply(_ as String) >> { 42 }
        and: "the result of transform is passed to load"
            2 * l.apply(_ as Integer) >> "bar"
        and: "the log is written to"
            1 * log.info(_ as String)
    }
}
