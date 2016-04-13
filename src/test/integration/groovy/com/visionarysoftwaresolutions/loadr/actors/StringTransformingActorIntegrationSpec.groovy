package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.StringTransformingActor
import com.visionarysoftwaresolutions.loadr.api.CloseableRepository
import org.slf4j.Logger

import java.util.function.Function

class StringTransformingActorIntegrationSpec extends spock.lang.Specification {
    def "rejects null inputs"() {
        when: "I try to construct with a null"
            new StringTransformingActor(r, l, t)
        then: "an IllegalArgumentException is thrown"
            IllegalArgumentException e = thrown()
        where: r                              | l            | t
               null                           | Mock(Logger) | Mock(Function)
               Mock(CloseableRepository)      | null         | Mock(Function)
               Mock(CloseableRepository)      | Mock(Logger) | null
    }

    def "transforms message and saves to Blackboard"() {
        given: "a transformation function"
            Function<String, Integer> transform = Mock(Function)
        and: "A CloseableRepository to save to"
            CloseableRepository<Integer> repo = Mock(CloseableRepository)
        and: "a StringTransformingActor"
            StringTransformingActor<Integer> toTest = new StringTransformingActor<>(repo, Mock(Logger), transform)
        and: "the actor is started"
            toTest.start()
        and: "A message to be sent"
            String message = "foo"
        when: "I send the message"
            toTest << message
        and: "I send a stop"
            toTest << "stop"
        and: "I wait for the actor to finish"
            [toTest]*.join()
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
        and: "a StringTransformingActor"
            StringTransformingActor<Integer> toTest = new StringTransformingActor<>(repo, log, transform)
        and: "the actor is started"
            toTest.start()
        and: "A message to be sent"
            String message = "foo"
        when: "I send the message"
            toTest << message
        and: "I send a stop"
            toTest << "stop"
        and: "I wait for the actor to finish"
            [toTest]*.join()
        then: "the transformation is applied"
            1 * transform.apply(message) >> { String s -> throw new IllegalArgumentException() }
        and: "the log is written"
            1 * log.error(_ as String)
    }

    /**
     * TODO: there is a Law of Demeter violation here I'm not happy about.
     * I want to communicate
     */
    def "communicates stop to Blackboard"() {
        given: "a transformation function"
            Function<String, Integer> transform = Mock(Function)
        and: "A Blackboard to save to"
            CloseableRepository<Integer> repo = Mock(CloseableRepository)
        and: "A log to write to"
            Logger log =  Mock(Logger)
        and: "a StringTransformingActor"
            StringTransformingActor<Integer> toTest = new StringTransformingActor<>(repo, log, transform)
        and: "the actor is started"
            toTest.start()
        when: "I tell the actor to stop"
            toTest << "stop"
        and: "I wait for the actor to finish"
            [toTest]*.join()
        then: "the transformation is applied"
            0 * transform.apply("stop")
        and: "the log is written"
            1 * repo.close()
    }
}
