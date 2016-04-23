package com.visionarysoftwaresolutions.loadr

import java.nio.file.Files
import java.nio.file.Paths

final class Fixtures {
    static File unorderedFile() {
        File temp = emptyFile()
        ["z".."a"].each {
            temp << "${it} \n"
        }
        temp
    }
    static File duplicatesFile() {
        File temp = tempFile()
        5.times {
            temp << "3 moose booster\n"
        }
        temp
    }

    static File tempFile() {
        File temp = emptyFile()
        temp << "1 cool dudebro\n2 lame brodawg"
        temp
    }

    static File whitespaceFile() {
        File temp = emptyFile()
        temp << "    \t \t\n\n\n"
        temp
    }

    static File emptyFile() {
        File temp = Files.createTempFile(Paths.get(System.getProperty("user.dir")), "foo", "bar").toFile()
        temp.deleteOnExit()
        temp
    }
}
