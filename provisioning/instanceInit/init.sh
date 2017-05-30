#!/bin/bash
baseDir=$(dirname $0)

( exec ${baseDir}/createUsers.sh )
sudo DEBIAN_FRONTEND=noninteractive apt-get -yq install python