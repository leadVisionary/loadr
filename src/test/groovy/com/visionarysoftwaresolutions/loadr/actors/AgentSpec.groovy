package com.visionarysoftwaresolutions.loadr.actors

import groovyx.gpars.actor.Actor

import java.util.function.Function

class AgentSpec extends spock.lang.Specification {

    def "rejects construction with null function"() {
        when: "I try to construct with a null function"
            new Agent<>(null)
        then: "I get an IllegalArgumentException"
            IllegalArgumentException e = thrown()
    }

    def "applies function on message and replies"() {
        given: "A function to apply"
            Function<?, ?> fun = Mock()
        and: "I construct an Agent to apply that function"
            Agent a = new Agent(fun)
        and: "I have a message to send"
             def message = new Object()
        when: "I send a message to the agent"
            a.onMessage(message)
        then: "the function is applied"
            1 * fun.apply(message)
    }

}
