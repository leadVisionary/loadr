package com.visionarysoftwaresolutions.loadr

import java.util.stream.Stream

class ReturnNonEmptyDistinctLinesSpec extends spock.lang.Specification {
    ReturnNonEmptyDistinctLines toTest

    def setup() {
        toTest = new ReturnNonEmptyDistinctLines()
    }

    def "rejects null file"() {
        when: "I try to apply on a null file"
            toTest.apply(null)
        then: "An IllegalArgumentException is thrown"
            IllegalArgumentException e = thrown()
    }

    def "rejects non-existant File"() {
        when: "I try to apply on a non-existant File file"
            toTest.apply(new File(UUID.randomUUID().toString()))
        then: "An IllegalArgumentException is thrown"
            IllegalArgumentException e = thrown()
    }

    def "rejects directory"() {
        when: "I try to apply on a non-existant File file"
            toTest.apply(new File(System.getProperty("user.dir")))
        then: "An IllegalArgumentException is thrown"
            IllegalArgumentException e = thrown()
    }

    def "returns empty stream for empty file"() {
        given: "a temporary file to process"
            File tempo = f
        when: "I try to apply to the file"
            Stream<String> result = toTest.apply(tempo)
        then: "the resulting Stream is empty"
            result.count() == 0
        where: f << [ Fixtures.emptyFile(), Fixtures.whitespaceFile()]
    }

    def "removes duplicates from file"() {
        given: "a temporary file to process"
            File tempo = Fixtures.duplicatesFile()
        when: "I try to apply to the file"
            Stream<String> result = toTest.apply(tempo)
        then: "the resulting Stream has de-duped entries"
            result.count() == 3
    }
}
