#!/usr/bin/env bash

source openmicroscopy/components/blitz/target/generated/resources/Classpath.sh
# same as openmicroscopy/components/blitz/target/classes/Classpath.sh
# (verified)

echo $CLASSPATH | awk -F':' '{ for (i = 0; ++i <= NF;) print $i }'
