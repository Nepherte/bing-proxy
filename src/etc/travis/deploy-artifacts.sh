#!/bin/bash

# This script compiles, packages and finally deploys the project's artifacts to
# the Ivy Repository. Tests are assumed to have run in a previous build stage.

# The location in which Ant is installed.
ANT_DIR="${TRAVIS_BUILD_DIR}/bin"

# The location in which the build file resides.
BUILD_FILE="${TRAVIS_BUILD_DIR}/src/etc/ant/build.xml"

function deploy_to_ivy() {
    "${ANT_DIR}/ant" -Dssh.key.password=${SSH_KEY_PASSWORD} -Dtest.skip=true -f "${BUILD_FILE}" publish
}

# -e: exit script immediately when a command returns a non-zero value.
set -e

deploy_to_ivy

