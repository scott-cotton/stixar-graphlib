#!/bin/sh

#
# set up environment for everything. In particular, define CLASSPATH
# and JAVA environmental variables
#


#
# set up kirkos jars
#
CLASSPATH=${CLASSPATH}:/Users/scott/Dev/stixar-graphlib/dist/lib/stixar-0.0.1-alpha.jar


#
# find java
#
if [ -z ${JAVA_HOME} ]; then
    JAVA=${JAVA_HOME}/bin/java;
else
    JAVA=`which java`;
fi

if [ ! -x ${JAVA} ]; then
    echo "couldn't find java, please set JAVA_HOME variable accordingly"
    echo "or make sure your path has an OK java"
    exit 1;
fi




