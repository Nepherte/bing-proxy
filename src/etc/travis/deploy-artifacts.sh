#!/bin/bash

# This script compiles, packages and finally deploys the project's artifacts to
# the Ivy Repository. Tests are assumed to have run in a previous build stage.

# The location in which the build file resides.
BUILD_FILE="${TRAVIS_BUILD_DIR}/pom.xml"

# The location in which the settings file resides.
SETTINGS_FILE="${TRAVIS_BUILD_DIR}/src/etc/maven/settings.xml"

function deploy_to_github() {
  mvn --settings "${SETTINGS_FILE}" --file "${BUILD_FILE}" -DskipTests deploy
}

# -e: exit script immediately when a command returns a non-zero value.
set -e

deploy_to_github