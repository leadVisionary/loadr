package com.visionarysoftwaresolutions.loadr.actors

import com.visionarysoftwaresolutions.loadr.actors.extract.SendLinesOfFileToActorCommand
import groovyx.gpars.actor.StaticDispatchActor

import java.nio.file.Files
import java.nio.file.Paths

class SendLinesOfFileToActorCommandSpec extends spock.lang.Specification {

    def "rejects null actor"() {
        when: "constructed with a null actor"
            new SendLinesOfFileToActorCommand(null)
        then: "an exception is thrown"
            IllegalArgumentException e = thrown()

    }

    def "sends contents of file to actor"() {
        given: "An actor to send messages to"
            StaticDispatchActor<String> actor = Mock(StaticDispatchActor)
        and: "and a command to send messages"
            SendLinesOfFileToActorCommand toTest = new SendLinesOfFileToActorCommand(actor)
        and: "A file to read from"
            File temp = Files.createTempFile(Paths.get(System.getProperty("user.dir")), "fake", "test").toFile()
            temp.deleteOnExit()
        and: "some content in the file"
            String content = "foo\nbar\n"
            temp.append(content)
        when: "I execute the command"
            toTest.execute(temp)
        then: "the actor starts"
            1 * actor.start()
        and: "receives the file contents and stop"
            2 * actor.send(_ as String)
    }
}
