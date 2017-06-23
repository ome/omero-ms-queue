#!/usr/bin/env bash

source openmicroscopy/components/blitz/target/generated/resources/Classpath.sh

java -cp $CLASSPATH ome.formats.importer.cli.CommandLineImporter "$@"
