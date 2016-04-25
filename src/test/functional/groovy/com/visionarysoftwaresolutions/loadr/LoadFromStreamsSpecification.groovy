package com.visionarysoftwaresolutions.loadr

import com.visionarysoftwaresolutions.loadr.api.Command
import groovy.transform.Immutable
import org.slf4j.Logger
import streams.StreamBasedLoadFromFileCommand

import java.nio.file.Paths
import java.util.function.Function
import java.util.stream.Stream

class LoadFromStreamsSpecification extends spock.lang.Specification {

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
            Function<File, Stream<String>> extract = new ReturnNonEmptyDistinctLines()
        and: "a transform function to apply on each line"
            Function<String, Dog> transform = new Function<String, Dog>() {
                @Override
                Dog apply(String s) {
                    def parsed = s.split(",")
                    new Dog(id: parsed[0] as long,
                            name: parsed[1],
                            breed: parsed[2],
                            color: parsed[3])
                }
            }
        and: "a memoizing function"
            Function<Dog, String> load = new Function<Dog, String>() {
                def store = []
                @Override
                String apply(Dog dog) {
                    store << dog
                    dog.toString()
                }
            }
        and: "A logger"
            Logger log = Mock(Logger)
        when: "I make an LoadFromFileViaActorsCommand"
            Command<File> com =
                    new StreamBasedLoadFromFileCommand<Dog>(log, 5, extract, transform, load)
        and: "I execute the Command"
            com.execute(input)
        then: "the data is stored"
            load.store.size() == 21
    }
}
