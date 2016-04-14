package com.visionarysoftwaresolutions.loadr.actors.extract

import groovyx.gpars.actor.StaticDispatchActor

import java.nio.file.Files
import java.util.function.Supplier

class LoadFromFileViaActorsCommandSpec extends spock.lang.Specification {

    def "rejects construction with null Supplier"() {
        when: "I construct with null Supplier"
            new LoadFromFileViaActorsCommand(null)
        then: "an IllegalArgumentException is thrown"
            IllegalArgumentException e = thrown()
    }

    def "throws IllegalArgumentException for null file or non-file"() {
        given: "A Supplier for a StaticDispatchActor of Files"
            Supplier<StaticDispatchActor<File>> supplier = Mock(Supplier)
        and: "A LoadFromFileViaActorsCommand to test"
            LoadFromFileViaActorsCommand toTest = new LoadFromFileViaActorsCommand(supplier)
        when: "I try to pass a null file"
            toTest.execute(file)
        then: "an IllegalArgumentException is thrown"
            IllegalArgumentException e = thrown()
        where: file << [null, new File(".")]
    }

    def "throws IllegalStateException when Supplier feeds bull"() {
        given: "A Supplier for a StaticDispatchActor of Files"
            Supplier<StaticDispatchActor<File>> supplier = Mock(Supplier)
        and: "A LoadFromFileViaActorsCommand to test"
            LoadFromFileViaActorsCommand toTest = new LoadFromFileViaActorsCommand(supplier)
        and: "a File to work on"
            File temp = Files.createTempFile(".", "foo").toFile()
            temp.deleteOnExit()
        when: "I try to pass the file"
            toTest.execute(temp)
        and: "the Supplier returns null"
            1 * supplier.get() << null
        then: "an IllegalStateException is thrown"
            IllegalStateException e = thrown()
    }

    class FileTrappingActor extends StaticDispatchActor<File> {
        File file

        @Override
        void onMessage(File message) {
            file = message
            stop()
        }
    }

    def "sends File to actor given by Supplier"() {
        given: "A Supplier for a StaticDispatchActor of Files"
            Supplier<StaticDispatchActor<File>> supplier = Mock(Supplier)
        and: "an Actor that could work on the file"
            FileTrappingActor actor = new FileTrappingActor()
        and: "A LoadFromFileViaActorsCommand to test"
            LoadFromFileViaActorsCommand toTest = new LoadFromFileViaActorsCommand(supplier)
        and: "a File to work on"
            File temp = Files.createTempFile(".", "foo").toFile()
            temp.deleteOnExit()
        when: "I try to pass the file"
            toTest.execute(temp)
        then: "the Supplier returns an Actor"
            1 * supplier.get() >> actor
        and: "the actor lifecycle works"
            actor.file == temp
    }
}
