# Development Dockerfile for omero-ms-queue
# -----------------------------------------
# This dockerfile can be used to build an
# omero-ms-queue distribution which can then be run
# within a number of different Docker images.

# By default, building this dockerfile will use
# the IMAGE argument below for the runtime image.
ARG BUILD_IMAGE=openjdk:8

# To build code with other runtimes
# pass a build argument, e.g.:
#
#   docker build --build-arg BUILD_IMAGE=openjdk:9 ...
#

# The produced /src directory will be copied the
# RUN_IMAGE for end-use. This value can also be
# set at build time with --build-arg RUN_IMAGE=...
ARG COMPONENT=server
ARG RUN_IMAGE=openmicroscopy/omero-${COMPONENT}:latest


FROM ${BUILD_IMAGE} as build
USER root
RUN apt-get update \
 && apt-get install -y ant gradle maven vim

# openjdk:8 is "stretch" or Debian 9
RUN id 1000 || useradd -u 1000 -ms /bin/bash build

# TODO: would be nice to not need to copy .git since it invalidates the build frequently and takes more time
COPY .git /src/.git

COPY buildSrc /src/buildSrc
COPY build.gradle /src/
COPY components /src/components
COPY gradle /src/gradle
COPY gradlew /src/
COPY settings.gradle /src/
RUN chown -R 1000 /src
USER 1000
WORKDIR /src

RUN ./gradlew build :packager:release -x test
RUN tar xzf components/packager/build/distributions//ome-smuggler-1.1.0-beta.tgz -C /tmp
RUN cp -r components/server/src/test/scripts/http-import \
        /tmp/ome-smuggler/
RUN cd /tmp/ome-smuggler/http-import/ && chmod +x get delete && chmod +x request-import list-failed-imports && chmod +x list-failed-mail
CMD ["/tmp/ome-smuggler/bin/run.sh"]
