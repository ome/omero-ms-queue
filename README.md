OME Smuggler
============
> Sneakily imports image goodies into OMERO without paying MRI session duties.

[![Build Status](https://travis-ci.org/c0c0n3/ome-smuggler.svg?branch=master)](https://travis-ci.org/c0c0n3/ome-smuggler)
[![codecov](https://codecov.io/gh/c0c0n3/ome-smuggler/branch/master/graph/badge.svg)](https://codecov.io/gh/c0c0n3/ome-smuggler)


Idea
----
The basic idea: a Web-based work queue to run tasks on behalf of OMERO clients.
A task is queued, then run and as it runs a URL is available from which to get
status updates. If the task fails, it may be retried. On completion, an email
report is sent to the interested parties. This is Smuggler’s life purpose—at
least for now. And for now, the only task Smuggler knows how to run is an OMERO
import. But it should be possible to add any other task without too much sweat
using the same run/retry/notify mechanism.


Contributing
------------
Want to hack Smuggler to pieces? Or contribute a couple of tweaks or a bug fix?
You're welcome to fork the repo and submit a pull request.
If you're planning to do open-heart surgery, you may find it useful to read the
developer docs over here:

* [http://c0c0n3.github.io/ome-smuggler/](http://c0c0n3.github.io/ome-smuggler/)

And by the way, if you're looking for the docs sources, we keep the whole lot
in the `gh-pages` branch:

* [https://github.com/c0c0n3/ome-smuggler/tree/gh-pages](https://github.com/c0c0n3/ome-smuggler/tree/gh-pages)

The docs site is generated from the sources in the same branch so that GitHub
can kindly publish it as a GitHub project site. To find out how, start from
the README in the `gh-pages` branch...


Build & Run...for the loo
-------------------------
Build and test everything:

    ./gradlew build

Use `gradlew <task>` (Unix; `gradlew.bat` for Windows) for finer control over
building, testing, etc. This lists all available build tasks:

    ./gradlew tasks

Ours is a Gradle multi-project build, 

    ./gradlew projects

lists all the build projects. Each of them comes with its own build you can
run independently using `gradlew :<project>:<task>`; for example

    ./gradlew :server:test

runs all the tests in the `server` project. If you feel adventurous and want
to actually run Smuggler, take our [whirlwind tour](http://c0c0n3.github.io/ome-smuggler/docs/content/examples/whirlwind-tour.html)!


Tricksy Eclipsie, Have no IDEA!
-------------------------------
Using Eclipse or IDEA? With recent versions you should be able to import the
entire Gradle multi-project build seamlessly as a Gradle project. If that
doesn't work for you, try adding the Gradle Eclipse or IDEA plugin to each
build project, which you can do in the root `build.gradle` file:

    allprojects {
        apply plugin: 'eclipse'
        apply plugin: 'idea' 
        ...

Then run:

    ./gradlew eclipse

(or `./gradlew idea` for IDEA) and import your Git checkout root directory
into Eclipse (IDEA) as an existing project.
If you're unhappy with the result, you'll have to have a look at our build
files and create the projects manually in your IDE. Give me a shout if you
need help to get you going! (I'm deaf.)

