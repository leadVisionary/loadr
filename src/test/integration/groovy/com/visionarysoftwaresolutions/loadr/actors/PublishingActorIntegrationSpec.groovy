package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.api.PublishCommand

class PublishingActorIntegrationSpec extends spock.lang.Specification {
    PublishCommand<?> command

    def setup() {
        command = Mock()
    }

    def "rejects null command"() {
        when: "a null command is given on construction"
            new PublishingActor(null)
        then: "an IllegalArgumentException is thrown"
            IllegalArgumentException e = thrown()
    }

    def "uses the command when a message is sent"() {
        given: "A PublishingActor configured with a command"
            PublishingActor<?> toTest = new PublishingActor<>(command)
        and: "the actor is started"
            toTest.start()
        and: "a message to be sent"
            def message = Mock(Object)
        when: "I send the actor a message"
            toTest << message
        and: "I tell the actor to stop"
            toTest.stop()
        and: "I wait for the actor to complete"
            toTest.join()
        then: "the command is invoked"
            1 * command.execute(message)
    }

}
