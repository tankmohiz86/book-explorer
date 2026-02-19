#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF)
# Maven Wrapper startup script
# ----------------------------------------------------------------------------
set -e

MAVEN_WRAPPER_PROPERTIES=".mvn/wrapper/maven-wrapper.properties"
DISTRIBUTION_URL=$(grep distributionUrl "$MAVEN_WRAPPER_PROPERTIES" | cut -d= -f2-)
WRAPPER_JAR=".mvn/wrapper/maven-wrapper.jar"

# If wrapper jar exists, use it
if [ -r "$WRAPPER_JAR" ]; then
    exec java -jar "$WRAPPER_JAR" "$@"
fi

# Otherwise, try to use mvn directly
if command -v mvn > /dev/null 2>&1; then
    exec mvn "$@"
fi

echo "ERROR: Maven not found. Please install Maven or run via Docker."
exit 1
