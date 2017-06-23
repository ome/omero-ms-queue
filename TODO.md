TODO (hopefully soon!) 
====

Security
--------
* Session Key. Ends up in the HornetQ journal on disk; if an attacker gains
access to the disk, they can decode the journal file and grab the session
key.
* Actuator. End points access needs to be restricted; be strict about what
info is published. Ditto for JMX.
* Import Report. The import status URL lets you access the import run's log.
Not a good idea as the CLI importer may output sensitive information, most
notably the session key!
* HTTPS. Needs to be enabled for production deployment; clients shouldn't be
able to open a HTTP connection to POST an import request as it contains the
session key, a yummy bite for eavesdroppers.
* Access Control. Non-existent; find out what users require.
* Loopholes. Probably many. The code was written quickly, run a thorough
security audit!

Session Keep Alive
------------------
Session key expires after 10 mins. Ping server periodically until import
request is fetched from the queue to make sure we have a valid session
key at the time we process the request and run the import.
Snag: OMERO is down for more than 10 mins, can't keep session alive, import
fails permanently, angry users, oh dear!
Ask the friendly OMElings if there's a simple way out, e.g. use a long-lived
import token (only valid to import a specific file for a given user) instead
of the session key?

Import Target Resolution
------------------------
Introduce URI resolution strategies so that the import target URI specified
by the client can be mapped to a location from which to fetch image(s) data.
This could be a network share visible both to client and server or perhaps
even a URL from which to download data---e.g. HTTP, FTP, etc.

Clustering, Fail-over, Backup
-----------------------------
Allow administrator to configure these features. For the most part, we just
need to tweak and expose HornetQ configuration. Low-hanging fruit.

OMERO Integration
-----------------
* Should the import run in the same JVM as the import server? Or should it be
a separate process as it is now? Each approach has its pros and cons, some
serious forehead banging needed to find the best solution.
* Should we write our own import client along the lines of the CLI importer?
This would give us finer control over the import which we're going to need
if we want to provide better feedback to the users or explore architectures
for large-scale imports---e.g. parallel/distributed, map/reduce, etc. Also,
we could recover from broken imports (e.g. retry if OMERO is down) or resume
partial file transfers.
* Should the import server become part of the OME software suite? A common
import platform that could be used both by Insight and the Web Client? The
queue could be used as a distributed event bus so that it'd become possible
to import from the Web client too.

Insight Integration
-------------------
* Aim straight before pulling the import trigger! We POST the import request
too early, we should wait for Bio-Formats to scan the import candidates and
decide what is importable so that if the user accidentally selects the wrong
file they can get an error message and change their selection. This avoids
POSTing imports that are doomed to fail.
* Import server option. Should it be an option? i.e. the user can choose at
import time if the import should go straight to OMERO or it should be offloaded
to the import server. Perhaps too confusing for most users, possibly useful to
lab managers who import on behalf of other users?
* Import server config. How is Insight supposed to find the import server's
address if an import server is at all available? Options range from `zeroconf`
to a plain old entry in the configuration file---a bit of a problem when some
clients should use an import server while others another server though.
(Note. This is not an issue at MRI as both the import server and Insight will
be deployed on each acquisition workstation, with Insight preconfigured to
to connect to the import server on `localhost`.)

Task Queue
----------
* Generalise the OMERO import so that the server can be used to run generic
tasks. A task is queued, then run and as it runs a URL is available from which
to get status updates. On completion, a report email is sent to the interested
parties.
* Add file copy and transfer tasks. A general-purpose way to move data out of
an acquisition workstation, perhaps using Globus/GridFTP.

World Domination
----------------
Discuss plans to take over the world and build a spacecraft to go to Titan.
I'm not at liberty to detail the plans here as I work for the FBI (French Bio
Imaging!) and, being the FBI, we like to keep our secrets secret, you know.

Technical Debt
--------------
Erm, uh well, this is going to spoil our plan for a weekend on Titan.
But we *really* need to make the code more robust, add logging and proper
exception handling. And as we're at it, why not consider

* Bio-Formats scan. Insight does it, the CLI importer does it, we should do it
too! The best place to implement it would be right before we put the import
request on the queue: if the file is not importable, bail out and tell the
client so they can change their selection. (Kinda what Insight does, but we
move the functionality server-side so clients don't need to use Bio-Formats.)
* Configuration. Review and consolidate. Start using profiles so we can run
the server in `Dev` mode, see below.
* `Dev` mode. Add sneaky beans to run the server with no HornetQ persistence
and no-op mail client---perhaps it could write the mail message to `stdout` or
put it on another queue. Only available when running with the `Dev` profile.
* Spring Booty JMS auto-config. Only used out of convenience to create the
embedded HornetQ instance, but it comes with the JMS baggage which we don't
need as we're using HornetQ's core API. Look at what they do in this package:
`org.springframework.boot.autoconfigure.jms.hornetq`; instead of setting up
a `org.hornetq.jms.server.embedded.EmbeddedJMS`, we should rather instantiate
its super-class `EmbeddedHornetQ` and get rid of the dependency on the HornetQ
JMS server jar in our `build.gradle`.
* ...[will add more to this list as I go along]
