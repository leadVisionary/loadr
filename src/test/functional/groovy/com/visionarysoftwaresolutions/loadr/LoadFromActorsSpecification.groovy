package com.visionarysoftwaresolutions.loadr

import com.visionarysoftwaresolutions.loadr.actors.Agent
import com.visionarysoftwaresolutions.loadr.actors.LoadFromFileViaActorsCommand
import com.visionarysoftwaresolutions.loadr.api.Command
import groovy.transform.Immutable
import groovyx.gpars.actor.StaticDispatchActor

import java.nio.file.Paths
import java.util.function.Function
import java.util.function.Supplier
import java.util.stream.Collectors
import java.util.stream.Stream

class LoadFromActorsSpecification extends spock.lang.Specification {
    Supplier<StaticDispatchActor<File>> fileActorSupplier
    Supplier<StaticDispatchActor<Stream<String>>> transformActorSupplier
    Supplier<StaticDispatchActor<Stream<Dog>>> loadActorSupplier

    @Immutable
    class Dog {
        long id
        String name
        String breed
        String color
    }

    def "can extract from a file, transform into an object, load into memory"() {
        given: "A file containing data"
            File input = Paths.get(System.getProperty("user.dir"), "src", "test", "resources", "test_data.csv").toFile()
        and: "a function to act on that file"
            Function<File, Stream<String>> process = new ReturnNonEmptyDistinctLines()
        and: "a Supplier that has a function which gets that file"
            fileActorSupplier = new Supplier<StaticDispatchActor<File>>() {
                @Override
                StaticDispatchActor<File> get() {
                    return new Agent<File, Object>(process)
                }
            }
        and: "a transform function to apply on each line"
            Function<String, Dog> transform = { String s ->
                def parsed = s.split(",")
                new Dog(id: parsed[0] as long,
                        name: parsed[1],
                        breed: parsed[2],
                        color: parsed[3])
            }
        and: "a Supplier which has an Agent that acts on that transform"
            transformActorSupplier = new Supplier<StaticDispatchActor<Stream<String>>>() {
                @Override
                StaticDispatchActor<Stream<String>> get() {
                    return new Agent<Stream<String>, Stream<Dog>>(new StringTransformer<Dog>(transform))
                }
            }
        and: "a memoizing function"
            Function<Stream<Dog>, Void> load = new Function<Stream<Dog>, Void>() {
                def store = []
                @Override
                Void apply(Stream<Dog> dogStream) {
                    store.addAll(dogStream.collect(Collectors.toList()))
                    return
                }
            }
        and: "A Supplier which has an Agent that acts on the load"
            loadActorSupplier = new Supplier<StaticDispatchActor<Stream<Dog>>>() {
                @Override
                StaticDispatchActor<Stream<Dog>> get() {
                    return new Agent<Stream<Dog>, Object>(load)
                }
            }
        when: "I make an LoadFromFileViaActorsCommand"
            Command<Dog> com =
                    new LoadFromFileViaActorsCommand<Dog>(fileActorSupplier, transformActorSupplier, loadActorSupplier)
        and: "I execute the Command"
            com.execute(input)
        then: "the data is stored"
            load.store.size() == 21
    }
}
