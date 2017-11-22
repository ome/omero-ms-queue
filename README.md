OMERA
=====
> OME Reactive Architecture.

[![Build Status](https://api.travis-ci.org/openmicroscopy/omero-ms-queue.svg?branch=master)](https://travis-ci.org/openmicroscopy/omero-ms-queue)
[![codecov](https://codecov.io/gh/openmicroscopy/omero-ms-queue/branch/master/graph/badge.svg)](https://codecov.io/gh/openmicroscopy/omero-ms-queue)


Overview
--------
The basic idea: a framework to build scalable and resilient OMERO micro
services.

We use distributed, asynchronous message passing as a foundation for a
[reactive architecture][rman] with scalable, resilient, and composable
services. The below components provide the messaging functionality:

* [kew][kew]: a strongly-typed messaging API that lets you plug in and
use different back-end messaging systems such *Apache Artemis*, *RabbitMQ*,
or even *Vert.x*.
* [kew-artemis][kew-artemis]: an implementation of the messaging API backed
by *Apache Artemis* which doubles up as a basic container for asynchronous,
message-driven, micro services.

The arrangement of these two components is similar to that in a Ports and
Adaptors architecture (a.k.a. Hexagonal architecture): [kew][kew] defines
the ports and [kew-artemis][kew-artemis] provides the adaptors.

### Coming Soon
We're busy adding support for distributed (*NoSQL*) state. The plan is to
have an arrangement similar to the above with an abstract API backed by
pluggable state providers so to keep service logic independent of the
underlying framework. (The one provider we're definitely going to have
is *Redis*.)

A more ambitious plan is to leverage messaging and distributed state to
build a minimalist, light-weight, distributed stream processing component
that would let us easily string together OMERO tasks into a distributed
computation. (See [these notes][dlp] for the details.) Or we might consider
using a full-fledged framework such as *Apache Spark* instead...

So watch this place!


Contributing
------------
Want to hack this code to pieces? Or contribute a couple of tweaks or a bug
fix? You're welcome to fork the repo and submit a pull request.
If you're planning to do open-heart surgery, you may find it useful to read
the README files (pun intended!) of the various components as well as the
JavaDoc for the specific classes you'll be working on. Also each component
comes with lots and lots of unit, integration, and end-to-end tests which
may be worth looking at to get the hang of how each unit works. I suppose
this was a long way to say: we still don't have a proper documentation site!
Oh, did I mention you're welcome to contribute?


Build System
------------
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

runs all the tests in the `server` project.


IDE Support
-----------
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




[dlp]: https://github.com/openmicroscopy/omero-ms-queue/issues/4
    "Distributed List Processing"
[kew]: components/kew
    "Kew Component"
[kew-artemis]: components/kew-artemis
    "Kew Artemis Component"
[rman]: https://www.reactivemanifesto.org/
    "The Reactive Manifesto"

