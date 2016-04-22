package com.visionarysoftwaresolutions.loadr.actors

import groovyx.gpars.actor.StaticDispatchActor

import java.nio.file.Files
import java.util.function.Supplier
import java.util.stream.Stream

class LoadFromFileViaActorsCommandSpec extends spock.lang.Specification {
    Supplier<StaticDispatchActor<File>> extractor
    Supplier<StaticDispatchActor<Stream<String>>> transformer
    Supplier<StaticDispatchActor<Stream<?>>> loader

    def setup() {
        extractor = Mock()
        transformer = Mock()
        loader = Mock()
    }

    def "rejects construction with null extractor"() {
        when: "I construct with null extractor"
            new LoadFromFileViaActorsCommand(null, transformer, loader)
        then: "an IllegalArgumentException is thrown"
            IllegalArgumentException ex = thrown()
    }

    def "rejects construction with null transformer"() {
        when: "I construct with null transformer"
            new LoadFromFileViaActorsCommand(extractor, null, loader)
        then: "an IllegalArgumentException is thrown"
        IllegalArgumentException ex = thrown()
    }

    def "rejects construction with null loader"() {
        when: "I construct with null loader"
        new LoadFromFileViaActorsCommand(extractor, transformer, null)
        then: "an IllegalArgumentException is thrown"
        IllegalArgumentException ex = thrown()
    }

    def "throws IllegalArgumentException for null file or non-file"() {
        given: "A LoadFromFileViaActorsCommand to test"
            LoadFromFileViaActorsCommand toTest = new LoadFromFileViaActorsCommand(extractor, transformer, loader)
        when: "I try to pass a null file"
            toTest.execute(file)
        then: "an IllegalArgumentException is thrown"
            IllegalArgumentException e = thrown()
        where: file << [null, new File(".")]
    }

    def "throws IllegalStateException when Supplier feeds bull"() {
        given: "A LoadFromFileViaActorsCommand to test"
            LoadFromFileViaActorsCommand toTest = new LoadFromFileViaActorsCommand(extractor, transformer, loader)
        and: "a File to work on"
            File temp = Files.createTempFile(".", "foo").toFile()
            temp.deleteOnExit()
        when: "I try to pass the file"
            toTest.execute(temp)
        and: "the Supplier returns null"
            1 * extractor.get() << null
        then: "an IllegalStateException is thrown"
            IllegalStateException e = thrown()
    }

    class FileTrappingActor extends StaticDispatchActor<File> {
        File file

        @Override
        void onMessage(File message) {
            file = message
            reply(Files.lines(message.toPath()))
        }
    }

    class DumbTransformingActor extends StaticDispatchActor<Stream<String>> {

        @Override
        void onMessage(Stream<String> message) {
            reply(message.mapToInt { it.size() })
        }
    }

    class DumbLoadingActor extends StaticDispatchActor<Stream<Integer>> {

        @Override
        void onMessage(Stream<Integer> message) {
            message.reduce(0, { a, b -> a+b })

        }
    }

    def "sends File to actor given by Supplier"() {
        given: "an Actor that could work on the file"
            FileTrappingActor actor = new FileTrappingActor()
        and: "an Actor that could transform"
            StaticDispatchActor<Stream<String>> t = new DumbTransformingActor()
        and: "An Actor that could load"
            StaticDispatchActor<Stream<?>> l = new DumbLoadingActor()
        and: "A LoadFromFileViaActorsCommand to test"
            LoadFromFileViaActorsCommand toTest = new LoadFromFileViaActorsCommand(extractor, transformer, loader)
        and: "a File to work on"
            File temp = Files.createTempFile(".", "foo").toFile()
            temp.deleteOnExit()
        when: "I try to pass the file"
            toTest.execute(temp)
        then: "the extractor returns an Actor"
            1 * extractor.get() >> actor
        and: "the transformer returns an Actor"
            1 * transformer.get() >> t
        and: "the loader returns an Actor"
            1 * loader.get() >> l
        and: "the extractor gets the file"
            actor.file == temp
    }
}
