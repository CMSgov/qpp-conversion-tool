#!/bin/bash
set -ef -o pipefail

VPC_PATH=$1

SCRIPTPATH=$(dirname "$(readlink -f "$0")")
PARENT=$(dirname $SCRIPTPATH)
GRANDPARENT=$(dirname $PARENT)

PYENV_DIR=$GRANDPARENT/.pyenv

export PYENV_ROOT=$GRANDPARENT/.pyenv
export PATH="$PYENV_DIR/bin:$PATH"
eval "$(pyenv init -)"
eval "$(pyenv virtualenv-init -)"

pyenv activate corevpc

cd $GRANDPARENT
build-ami $VPC_PATH
