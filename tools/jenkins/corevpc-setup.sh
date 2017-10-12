#!/bin/bash
set -ef -o pipefail

# setup for corevpc
# use the corevpc virtualenv

SCRIPTPATH=$(dirname "$(readlink -f "$0")")
PARENT=$(dirname $SCRIPTPATH)
GRANDPARENT=$(dirname $PARENT)

PYENV_DIR=$GRANDPARENT/.pyenv

echo "PYENV_DIR $PYENV_DIR"
if [ ! -d "$PYENV_DIR" ]
then
  # get local pyenv version
  git clone --branch v1.1.3 https://github.com/pyenv/pyenv.git $PYENV_DIR
  git clone --branch v1.1.0 https://github.com/pyenv/pyenv-virtualenv.git $PYENV_DIR/plugins/pyenv-virtualenv
fi

# add new pyenv to the path to force local pyenv instead of global one
export PYENV_ROOT=$GRANDPARENT/.pyenv
export PATH="$PYENV_DIR/bin:$PATH"

eval "$(pyenv init -)"
eval "$(pyenv virtualenv-init -)"

pyenv install -s 3.5.3
if [ ! -d "$PYENV_DIR/versions/corevpc" ]
then
  pyenv virtualenv 3.5.3 corevpc
fi

pyenv activate corevpc

# corevpc should have been checked out by the jenkins script
cd $GRANDPARENT/corevpc/requirements
echo "Install python requirements"
pip install -q -r common.txt -r dev.txt -r test.txt
cd $GRANDPARENT/corevpc
echo "Set up CoreVPC python"
python setup.py -q develop
