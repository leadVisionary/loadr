package com.visionarysoftwaresolutions.loadr

import groovyx.gpars.actor.StaticDispatchActor

import java.nio.file.Files
import java.nio.file.Paths

class FileScanningActorIntegrationSpec extends spock.lang.Specification {
    class DummyActor extends StaticDispatchActor<String> {
        def messages = []

        @Override
        void onMessage(String message) {
            if (message == "stop") {
                stop()
            }
            messages << message
        }
    }

    def "rejects null dispatcher"() {
        when: "I try to conastruct with null"
            new FileScanningActor(null)
        then: "An IllegalArgumentException is thrown"
            IllegalArgumentException e = thrown()
    }

    def "can load from a file"() {
        given: "A file to read from"
            File temp = Files.createTempFile(Paths.get(System.getProperty("user.dir")), "fake", "test").toFile()
            temp.deleteOnExit()
        and: "some content in the file"
            String content = "1\tBoogie\tPit Bull\tBlack & White\n2\tChompie\tRottweiler Retriever\tBlack & Tan"
            temp.append(content)
        and: "An Actor to process the messages"
            StaticDispatchActor<String> processor = new DummyActor()
        and: "A FileScanningActor to process the file"
            FileScanningActor toTest = new FileScanningActor(processor)
        and: "the processor are started"
            processor.start()
        and: "file scanner is started"
            toTest.start()
        when: "I send the file to the FileScanningActor"
            toTest << temp
        and: "I wait for the actors to finish"
            [processor, toTest]*.join()
        then: "the processor receives the file contents and stop"
            processor.messages.containsAll(content.split("\n"))
    }
}
