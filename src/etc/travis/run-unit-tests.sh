#!/bin/bash

## This script runs the unit tests of this project. When a test fails, this
## results in an overall build failure on Travis CI as well.

# The location in which the build file resides.
BUILD_FILE="${TRAVIS_BUILD_DIR}/pom.xml"

# The location in which the settings file resides.
SETTINGS_FILE="${TRAVIS_BUILD_DIR}/src/etc/maven/settings.xml"

function run_unit_tests() {
  mvn --settings "${SETTINGS_FILE}" --file "${BUILD_FILE}" test
}

# -e: exit script immediately when a command returns a non-zero value.
set -e

run_unit_tests