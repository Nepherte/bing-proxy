#!/bin/bash

## This script runs the unit tests of this project. When a test fails, this
## results in an overall build failure on Travis CI as well.

# The location in which Ant is installed.
ANT_DIR="${TRAVIS_BUILD_DIR}/bin"

# The location in which the build file resides.
BUILD_FILE="${TRAVIS_BUILD_DIR}/src/etc/ant/build.xml"

function run_unit_tests() {
    "${ANT_DIR}/ant" -Dssh.key.password=${SSH_KEY_PASSWORD} -f "${BUILD_FILE}" test
}

# -e: exit script immediately when a command returns a non-zero value.
set -e

run_unit_tests