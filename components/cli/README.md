OME Command Line Interface
==========================
Command line tool to run OMERO library code.

Why?
----
Smuggler needs to call OMERO to keep an import session alive while the import
is queued and then to actually run the import when the time comes. We can't
use directly the Blitz library from Smuggler though as several of Blitz's
dependent libraries are also required by Smuggler but Smuggler uses later
versions---e.g. Blitz uses Spring 3 whereas Smuggler is on version 4.
To avoid nasty conflicts on the classpath, we have to run the Blitz code in
a separate JVM with its own classpath. This is just a stop-gap solution, we
can ditch this project as soon as OMERO starts using Spring 4.
