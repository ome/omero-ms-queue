Kew Artemis Provider
====================
> Typed, asynchronous messaging backed by Apache Artemis.


Overview
--------
This component provides three packages with distinct but related functionality:

* `qchan`. An implementation of a typed communication channel backed by
message queues as specified by the `kew.core.qchan.spi` interfaces.
* `runtime`. Drop-in replacement for Artemis stock instance servers which
also lets you embed a server in your own application, providing a basic
container for asynchronous, message-driven micro-services architectures.
* `config`. Programmatic, better-typed Artemis configuration that aims
to prevent some configuration pitfalls and simplify Artemis configuration
API.

Each package can be used just on its own or in combination with the others.
We look at each of them in detail below and then explain how to configure
Artemis logging and security.


Messaging Channels
------------------
The `qchan` package implements the various service-provider interfaces in
`kew.core.qchan.spi` to provide messaging channels backed by Apache Artemis.
It lets you send and receive messages from an Artemis queue asynchronously,
using typed communication channels as defined in `kew.core.msg`. Most of
the code in `qchan` comes straight from Smuggler's code base modulo a few
tweaks and slight generalisation.

### Usage
Before you can send and receive messages on a queue, you'll have to have
deployed that queue in Artemis. Assuming you've done that, the next step
is to establish a client session with the Artemis server:

        // Use Artemis core API to connect to the server.
        TransportConfiguration connector = ...
        ServerLocator locator =
                ActiveMQClient.createServerLocatorWithHA(connector);

        // Create our session wrapper; it automatically starts the session.
        ServerConnector session = ServerConnector(locator);

Note that there are many ways to create a server locator, depending on
whether you're running an Artemis standalone server or you have a cluster
instead. The example above assumes you're running a cluster.

Now you can create a factory to build messaging channels to send/receive
typed messages to/from your queue:

        // Use Artemis core API to specify how to get hold of your queue.
        CoreQueueConfiguration qConfig = new CoreQueueConfiguration()
                                        .setName("my/q")
                                        .setAddress("my/q");

        // Create a factory to exchange MyClass message instances.
        QChannelFactory<ArtemisMessage, MyClass> factory =
            new ArtemisQChannelFactory<>(session, qConfig);

To send a `MyClass` message you first have to build a message source.
Likewise for receiving you need a sink. Here's an example of building
both a source and a sink.

        // To build a source you have to specify how to serialize MyClass.
        // You could have e.g. generic Json serializers or whatever suits you.
        SinkWriter<MyClass, OutputStream> serializer = ...
        ChannelSource<MyClass> source = factory.buildSource(serializer);

        // To build a sink you have to specify how to deserialize MyClass.
        SourceReader<InputStream, MyClass> deserializer = ...
        ChannelSink<MyClass> consumer = ...
        MessageSink<ArtemisMessage, InputStream> sink =
            factory.buildSink(consumer, deserializer);

Both source and sink should be reused to send/receive as many messages as
possible and can be shared among threads. You'd typically set them up
upfront at application start up and then use them in your app. For example,
each source and sink is a singleton Spring bean in Smuggler and is
available for the entire lifetime of Smuggler's process.

With your source you can send `MyClass` messages asynchronously

        MyClass msg = ...
        source.send(msg);  // returns immediately

whereas your consumer gets fed the message on arrival. Note that producer,
consumer, and Artemis server can each live in a separate process. (But see
below about running your consumers directly inside an Artemis server.)
For working examples, have a look at the tests in the `end2end` package.
Smuggler's various services provide more advanced usage examples with
task scheduling and retries.


Embedded Artemis
----------------
The Artemis distribution comes with scripts to create server instances,
to configure them, and to start and stop them. This is the usual way of
running an Artemis server---or a cluster of them. But you can also embed
the server into your own process. The classes in our `runtime` package
let you do that easily. If you then also have your consumer tasks live
in the same process, you end up with a basic server container for asynchronous,
message-driven micro-services architectures. This way, you can easily
leverage Artemis clustering capabilities to load-balance micro-services
and do some rudimentary map/reduce.

For example, say you just want to run a task in parallel on multiple nodes.
Then you can turn task inputs into messages that get fed into a consumer
task you bundle with the server. You then spawn this server process on
multiple nodes with a symmetric cluster configuration where cluster members
share a "map" queue. When a client sends a stream of task inputs on the
"map" queue, these get distributed across cluster members and fed into
your consumer instances running on those servers. If you configure each
cluster member to have multiple "map" consumers, then messages are also
processed in parallel within each server instance. To make things slightly
more interesting, say your task produces an output you'd like to process
further. Then you could also deploy a "reduce" queue and a "reduce" consumer
to assemble the outputs that the "map" task would put on the "reduce" queue.
Again, all this mostly boils down to specifying a suitable Artemis cluster
configuration.

The tests in the `end2end` package come with a complete example of a two-node
cluster with a shared queue where messages are distributed round-robin between
the two nodes.


Core Configuration
------------------
To configure an Artemis server you can either use an XML file or the core
API for programmatic configuration. Either way you'll have to be careful
not to make silly mistakes like specifying transport properties not suitable
for a connector or acceptor, mismatching acceptor/connector pairs, omitting
a transport in a cluster configuration, and so on. Some of these mistakes
can be prevented by using a more typed configuration, which is what our
`config` package attempts to provide. While on the one hand, this package
avoids you making the mistakes that costed me hours of debugging, on the
other hand it also simplifies configuration quite a bit, especially when
it comes to clustering. As an added benefit, this package lets use both
XML and programmatic configuration at the same time so that, for example,
you could read in XML configuration and then tweak it programmatically.


Logging
-------
Artemis components use [JBoss logging][jboss-log-docs] to record their
activity. JBoss logging is a facade to a back-end logging framework that
is ultimately responsible for writing log records. The supported back-ends
are JBoss's own LogManager, Log4j, Logback, Slf4j, and JDK logging. You
can select which back-end to use by setting the `org.jboss.logging.provider`
JVM system property and adding the corresponding framework jars to your
class-path. (Look at the [LoggerProviders][jboss-logger-providers] class
for the details of the selection process and to know what values the sys
prop expects.) If `org.jboss.logging.provider` isn't set, then JBoss logging
scans the class-path to detect which back-end to use. If none of the supported
back-end classes is found, then the back-end defaults to JDK logging. Note
that JDK logging too has a plugin architecture where the built-in logging
provider can be replaced with an external one specified through the
`java.util.logging.manager` system property.

Artemis server instances created through the Artemis distribution scripts
are automatically configured to use JBoss's LogManager. So if you're using
the stock server, there's nothing to do to have proper logging in place.
(Unless of course, for some reason, you're not happy with JBoss's LogManager
as a back-end.) On the other hand, if you're using an Artemis server instance
embedded in your own application (see above), you should configure a logging
back-end. If you're happy with Logback, than just having its jars on your
class-path is enough as JBoss logging will detect it and configure it as
its back-end. For example, adding

        compile 'ch.qos.logback:logback-classic:1.1.11'

to your Gradle build file is enough to have logging in place. The same
applies to Artemis client-side logging: you'll have to configure a back-end
yourself. Note that the Artemis manual suggests using JBoss's LogManager as
an external plugin to JDK logging. Besides test cases, there's no reference
to JDK logging in their client code base, so I don't think you need to go
into the trouble, rather just add the jars of the framework you'd like to
use to your class-path. Indeed the same applies to the case of an embedded
Artemis server: the only references to JDK logging are in the instance
bootstrap class (`*.artemis.boot.Artemis`) which isn't used for an embedded
server anyway, so just adding jars to the class-path should be enough.
(Note the bootstrap class is used by the stock instance server though,
which is probably why the launch script not only adds JBoss's LogManager
jars to the class-path but also sets `java.util.logging.manager` to make
sure, I reckon, that any invocation of the JDK logging methods is ultimately
handled by JBoss's LogManager.)


Security
--------
TODO!!!




[jboss-log-docs]: https://access.redhat.com/documentation/en-us/red_hat_jboss_enterprise_application_platform/7.0/html/development_guide/logging_for_developers
    "JBoss EAP - Logging"
[jboss-logger-providers]: https://github.com/jboss-logging/jboss-logging/blob/master/src/main/java/org/jboss/logging/LoggerProviders.java
    "LoggerProviders class"
[jboss-logmanager]: https://github.com/jboss-logging/jboss-logmanager
    "JBoss LogManager on GitHub"
