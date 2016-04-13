package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.api.CloseableRepository
import org.slf4j.Logger

import java.util.function.Function

class TransformStringAndSaveToRepositoryCommandSpec extends spock.lang.Specification {
    def "rejects null inputs"() {
        when: "I try to construct with a null"
            new TransformStringAndSaveToRepositoryCommand(r, l, t)
        then: "an IllegalArgumentException is thrown"
        IllegalArgumentException e = thrown()
        where: r                         | l            | t
        null                      | Mock(Logger) | Mock(Function)
        Mock(CloseableRepository) | null         | Mock(Function)
        Mock(CloseableRepository) | Mock(Logger) | null
    }

    def "transforms message and saves to Blackboard"() {
        given: "a transformation function"
            Function<String, Integer> transform = Mock(Function)
        and: "A CloseableRepository to save to"
            CloseableRepository<Integer> repo = Mock(CloseableRepository)
        and: "a TransformStringAndSaveToRepositoryCommand"
            TransformStringAndSaveToRepositoryCommand<String> toTest = new TransformStringAndSaveToRepositoryCommand<>(repo, Mock(Logger), transform)
        and: "A message to be sent"
            String message = "foo"
        when: "I send the message"
            toTest.execute(message)
        then: "the transformation is applied"
            1 * transform.apply(message) >> message.length()
        and: "the repository is saved to"
            1 * repo.save(message.length())
    }

    def "writes to log on error"() {
        given: "a transformation function"
            Function<String, Integer> transform = Mock(Function)
        and: "A CloseableRepository to save to"
            CloseableRepository<Integer> repo = Mock(CloseableRepository)
        and: "A log to write to"
            Logger log =  Mock(Logger)
        and: "a TransformStringAndSaveToRepositoryCommand"
            TransformStringAndSaveToRepositoryCommand<String> toTest = new TransformStringAndSaveToRepositoryCommand<>(repo,log, transform)
        and: "A message to be sent"
            String message = "foo"
        when: "I send the message"
            toTest.execute(message)
        then: "the transformation is applied"
            1 * transform.apply(message) >> { String s -> throw new IllegalArgumentException() }
        and: "the log is written"
            1 * log.error(_ as String)
    }
}
