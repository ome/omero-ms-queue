Kew
===
> Messaging API backed by pluggable message-oriented middleware.

Overview
--------
This is a general-purpose component providing a messaging API backed
by pluggable message-oriented middleware. We should keep this component
independent of the various OME micro services in this repo and reusable
across projects.

The core messaging API is in `kew.core.msg` and defines messaging in
terms of abstract, typed communication channels. A message queue can
be used to provide an implementation of such channels. We do this in
`kew.core.qchan` but the implementation isn't tied to any particular
middleware. In fact, it depends on a set of service-provider interfaces
(`kew.core.qchan.spi`) that detail what an external messaging system
should provide. The actual implementation of these interfaces is
provided in a separate component, so that we can plug in and use
different messaging systems. At the moment we only have one provider
component that uses Apache Artemis (see [kew-artemis][kew-artemis]),
but going forward we might add more---e.g. RabbitMQ or even Vert.x.

Here's a high-level UML component diagram that shows the lay of the
land.

![High-Level Components](/docs/diagrams/kew-components.svg)

Most of the code was lifted from Smuggler's messaging component and
generalised just slightly. In fact, the original messaging design as
documented [here][smugs-msg] for the most part still stands and we
suggests you read about it before digging into the code.


Uses
----
Smuggler currently uses `kew` and [kew-artemis][kew-artemis] as a foundation
for resilient work queues that are the backbone of OMERO import and session
services as well as mail, file, and GC tasks. We could build similar work
queues for OMERO micro services that would provide

* feature calculation, populating annotations;
* rendering, thumbnails, and other cache loading;
* pyramid generation;
* search indexing;
* cleanup tasks (delete, etc);
* archiving/compressing old data.

(Refer to @joshmoore's design document for the details.)
In general, this messaging infrastructure is useful to build a distributed
system where scalability and resilience are achieved by **avoiding**:

* **space coupling**: direct links among communicating components
* **time coupling**: components must exist at the time communication
takes place

### Example: Web imports at MRI
In building Smuggler, we faced several challenges:

* stay responsive without interfering with running acquisitions
* reliably import large data sets
* overcome transient failures
* provide back-pressure mechanism to avoid OMERO overload
* ensure outcome notifications
* scale horizontally to dozens of machines

which required us to put in place a scalable and resilient architecture

![Smuggler's Architecture](/docs/diagrams/web-imports-at-mri.svg)

You can read more about Smuggler's high-level design [over here][smugs-hlv].




[kew-artemis]: components/kew-artemis
    "Kew Artemis Component"
[smugs-hlv]: http://c0c0n3.github.io/ome-smuggler/docs/content/design/high-level/index.html
    "High-Level View"
[smugs-msg]: http://c0c0n3.github.io/ome-smuggler/docs/content/design/messaging/index.html
    "Messaging"
