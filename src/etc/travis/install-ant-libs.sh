#!/bin/bash

## This script installs a newer version of Ant than what's available in Travis.
## Builds need an up-to-date version to run tests written in JUnit 5 framework.

# The version of Ant to install.
ANT_VERSION="1.10.5"

# The mirror from which to download Ant.
ANT_MIRROR="https://www.apache.org/dist/ant/binaries"

# The location in which to install Ant.
ANT_INSTALLATION_DIR="${TRAVIS_BUILD_DIR}/bin"

# The location in which the build file resides.
BUILD_FILE="${TRAVIS_BUILD_DIR}/src/etc/ant/build.xml"

function install_ant() {
    local ANT_ARCHIVE="apache-ant-${ANT_VERSION}-bin.tar.gz"
    local ANT_DOWNLOAD_URL="${ANT_MIRROR}/${ANT_ARCHIVE}"

    mkdir -p "${ANT_INSTALLATION_DIR}"
    pushd "${ANT_INSTALLATION_DIR}" > /dev/null

    echo "Downloading archive from ${ANT_MIRROR}..."
    wget -q ${ANT_DOWNLOAD_URL}

    echo "Extracting archive to ${ANT_INSTALLATION_DIR}..."
    tar -xzf ${ANT_ARCHIVE}; rm ${ANT_ARCHIVE}
    ln -s apache-ant-${ANT_VERSION}/bin/ant ant; popd > /dev/null
}

function install_ant_libs() {
    local ANT_BIN="${ANT_INSTALLATION_DIR}/ant"
    "${ANT_INSTALLATION_DIR}/ant" -f "${BUILD_FILE}" install
}

# -e: exit script immediately when a command returns a non-zero value.
set -e

install_ant
install_ant_libs