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
container for asynchronous, message-driven, micro services.
* `config`. Programmatic, better-typed Artemis configuration that aims
to prevent some configuration pitfalls and simplify Artemis configuration
API.

Each package can be used just on its own or in combination with the others.
We look at each of them in detail below and then explain how to configure
Artemis logging and security. Much of what we say below assumes you're
already familiar with Artemis; if you aren't, I'd recommend to at least
skim through [Artemis manual][artemis-man]!


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

```java
// Use Artemis core API to connect to the server.
TransportConfiguration connector = ...
ServerLocator locator =
        ActiveMQClient.createServerLocatorWithHA(connector);

// Create our session wrapper; it automatically starts the session.
ServerConnector session = ServerConnector(locator);
```

Note that there are many ways to create a server locator, depending on
whether you're running an Artemis standalone server or you have a cluster
instead. The example above assumes you're running a cluster.

Now you can create a factory to build messaging channels to send/receive
typed messages to/from your queue:

```java
// Use Artemis core API to specify how to get hold of your queue.
CoreQueueConfiguration qConfig = new CoreQueueConfiguration()
                                .setName("my/q")
                                .setAddress("my/q");

// Create a factory to exchange MyClass message instances.
QChannelFactory<ArtemisMessage, MyClass> factory =
            new ArtemisQChannelFactory<>(session, qConfig);
```

To send a `MyClass` message you first have to build a message source.
Likewise for receiving you need a sink. Here's an example of building
both a source and a sink.

```java
// To build a source you have to specify how to serialize MyClass.
// You could have e.g. generic Json serializers or whatever suits you.
SinkWriter<MyClass, OutputStream> serializer = ...
ChannelSource<MyClass> source = factory.buildSource(serializer);

// To build a sink you have to specify how to deserialize MyClass.
SourceReader<InputStream, MyClass> deserializer = ...
ChannelSink<MyClass> consumer = ...
MessageSink<ArtemisMessage, InputStream> sink =
            factory.buildSink(consumer, deserializer);
```

Both source and sink should be reused to send/receive as many messages as
possible and can be shared among threads. You'd typically set them up
upfront at application start up and then use them in your app. For example,
each source and sink is a singleton Spring bean in Smuggler and is
available for the entire lifetime of Smuggler's process.

With your source you can send `MyClass` messages asynchronously

```java
MyClass msg = ...
source.send(msg);  // returns immediately
```

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

### Examples
The tests in the `end2end` package come with a complete example of a two-node
cluster with a shared queue where messages are distributed round-robin between
the two nodes as well as a simpler example of embedding a standalone Artemis
instance. These tests use the classes in our `config` and `runtime` packages
to configure and embed Artemis.

You can find yet another example of an embedded standalone server in Smuggler
(`*.config.wiring.artemis` package) which also uses our `config` and `runtime`
packages. For a variation on the theme, you can [go back in time][smugs] and
look at how Smuggler used to embed Artemis using SpringBoot: there you'll see
we only used the `config` package to customise the Artemis JMS instance
auto-configured by SpringBoot. (To see how Artemis auto-configuration works
in SpringBoot, look at the `org.springframework.boot.autoconfigure.jms.artemis`
package.)


Core Configuration
------------------
To configure an Artemis server you can either use an XML file or the core
API for programmatic configuration. Either way you'll have to be careful
not to make silly mistakes like specifying transport properties not suitable
for a connector or acceptor, mismatching acceptor/connector pairs, omitting
a transport in a cluster configuration, and so on. Some of these mistakes
can be avoided by using a more typed configuration, which is what our
`config` package attempts to provide. On the one hand, this package tries
to stop you making the mistakes that costed me hours of debugging, on the
other hand it also simplifies configuration quite a bit, especially when
it comes to clustering. As an added benefit, this package lets you use both
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
Artemis offers role-based access control (authentication and authorisation)
for queue addresses while network connections can be secured through SSL/TSL.
We're going to sum up Artemis security configuration key points below and
explain how to secure an embedded Artemis instance.

### Authentication
Authentication happens through [JAAS][jazz]. In the JAAS `*login.config`
file, you define an application entry (usually named `activemq`) with the
login modules to use for authenticating users. To make JAAS use your config
file, you have to set a system property as in the example below:

    java.security.auth.login.config = /path/to/your/login.config

Typically this is done through a JVM `-D` startup argument, but you can
also do it programmatically during the early phase of application startup
if embedding Artemis. The next step is to tell Artemis to use the configured
JAAS app entry by specifying its name (e.g. `activemq`) as the value of
the `domain` attribute of the `jaas-security` tag in the `bootstrap.xml`
file. Additionally, when using SSL/TSL, you can have Artemis use SSL/TSL
certificates to authenticate clients connecting through SSL/TSL. To do
this, you have to add another application entry to `*login.config` and
then set its name as the value of the `certificate-domain` attribute of
the `jaas-security` tag in `bootstrap.xml`. Note that when embedding Artemis,
you can't use a `bootstrap.xml` file, so the setting of those XML `*domain`
attributes has to be done in code by instantiating the embedded server
with a suitable security manager---e.g. `ActiveMQJAASSecurityManager` lets
you set both attributes. Our `config` package lets you set both attributes
too and might be a better alternative to using a concrete security manager
implementation directly.

### Authorisation
Authorisation is specified through Artemis core configuration by granting
access permissions to roles on specific addresses. You can do this in XML
by editing the `broker.xml` main configuration file or do it programmatically,
e.g. using our `config` package, if embedding Artemis in your own app.

### Examples
The Artemis distribution comes with an `artemis` script you can use to
create a directory with scripts and initial configuration to run an Artemis
server instance. If you have a look at the launch script in the generated
`bin` directory, you'll see the script starts a JVM to run the server with
a `-D` argument pointing to the JASS `login.config` in `etc/` where you'll
also find property files defining users and roles as well as `bootstrap.xml`
and `broker.xml` files. In `bootstrap.xml` you should be able to see the
setting of the `domain` attribute whereas `broker.xml` should have a
`security-settings` section with access permissions. Also, the `examples`
directory in the Artemis distribution is packed with lots of examples you
might want to look at.

Our tests in the `end2end` package come with a complete example of a secured
embedded Artemis instance configured programmatically using the `config`
package.




[artemis-man]: https://activemq.apache.org/artemis/docs/latest/
    "Apache ActiveMQ Artemis User Manual"
[jazz]: https://en.wikipedia.org/wiki/Java_Authentication_and_Authorization_Service
    "Java Authentication and Authorization Service"
[jboss-log-docs]: https://access.redhat.com/documentation/en-us/red_hat_jboss_enterprise_application_platform/7.0/html/development_guide/logging_for_developers
    "JBoss EAP - Logging"
[jboss-logger-providers]: https://github.com/jboss-logging/jboss-logging/blob/master/src/main/java/org/jboss/logging/LoggerProviders.java
    "LoggerProviders class"
[jboss-logmanager]: https://github.com/jboss-logging/jboss-logmanager
    "JBoss LogManager on GitHub"
[smugs]: https://github.com/c0c0n3/omero-ms-queue/commit/cda8e0898e3d2bfb1a0ddeeb49c13bf25d7a7597
    "commit cda8e08: use kew-artemis embedded server instead of spingboot's."
