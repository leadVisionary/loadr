# loadr
A generic Extract/Transform/Load framework.
https://en.wikipedia.org/wiki/Extract,_transform,_load

The Framework API is built around the concepts of
- COMMAND https://sourcemaking.com/design_patterns/command
- REPOSITORY http://www.methodsandtools.com/archive/archive.php?id=97p2

The two implementations offered are
- Actor-based using GPars http://www.gpars.org/guide/
-- com.visionarysoftwaresolutions.loadr.actors.ActorBasedFileProcessingCommandSupplier
- JDK8 Stream based
-- com.visionarysoftwaresolutions.loadr.streams.StreamBasedLoadFromFileCommand

Usage is pretty simple: make an instance, tell the command to execute.
The constructors tell the tale of what you need to pass.

A sample application structure can be seen in the functional tests of 
each.
