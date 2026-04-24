#!/bin/sh
# Apache Maven Wrapper startup script
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements. See NOTICE for details.

set -e

# Resolve script base directory
BASEDIR=$(cd "$(dirname "$0")" && pwd)

WRAPPER_JAR="$BASEDIR/.mvn/wrapper/maven-wrapper.jar"
WRAPPER_PROPERTIES="$BASEDIR/.mvn/wrapper/maven-wrapper.properties"
MAVEN_USER_HOME="${MAVEN_USER_HOME:-$HOME/.m2}"

# Find Java
if [ -n "$JAVA_HOME" ]; then
    JAVA_CMD="$JAVA_HOME/bin/java"
    if [ ! -x "$JAVA_CMD" ]; then
        echo "ERROR: JAVA_HOME points to an invalid directory: $JAVA_HOME" >&2
        exit 1
    fi
else
    JAVA_CMD="java"
    if ! command -v java >/dev/null 2>&1; then
        echo "ERROR: java not found in PATH and JAVA_HOME is not set." >&2
        exit 1
    fi
fi

# Download wrapper JAR if not present
if [ ! -f "$WRAPPER_JAR" ]; then
    WRAPPER_URL=$(grep 'wrapperUrl' "$WRAPPER_PROPERTIES" | sed 's/wrapperUrl=//')
    echo "Downloading Maven Wrapper JAR..."
    if command -v curl >/dev/null 2>&1; then
        curl -fsSL -o "$WRAPPER_JAR" "$WRAPPER_URL"
    elif command -v wget >/dev/null 2>&1; then
        wget -q -O "$WRAPPER_JAR" "$WRAPPER_URL"
    else
        echo "ERROR: curl or wget is required to download the Maven Wrapper." >&2
        exit 1
    fi
fi

exec "$JAVA_CMD" \
    -classpath "$WRAPPER_JAR" \
    org.apache.maven.wrapper.MavenWrapperMain \
    -Dmaven.multiModuleProjectDirectory="$BASEDIR" \
    "$@"
