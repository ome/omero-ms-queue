Kew
===
This is a general-purpose component providing a messaging API backed
by pluggable message-oriented middleware.

The core messaging API is in `kew.core.msg` and defines messaging in
terms of abstract, typed communication channels. A message queue can
be used to provide an implementation of such channels. We do this in
`kew.core.qchan` but the implementation isn't tied to any particular
middleware. In fact, it depends on a set of service-provider interfaces
(`kew.core.qchan.spi`) that detail what an external messaging system
should provide. The actual implementation of these interfaces is
provided in a separate component, so that we can plug in and use
different messaging systems. At the moment we only have one provider
component that uses Apache Artemis (see `kew-artemis` component), but
going forward we might add more---e.g. RabbitMQ or even Vert.x.

Most of the code was lifted from Smuggler's messaging component and
generalised just slightly. We should keep this component independent
of the various OME micro services in this repo and reusable across
projects.
