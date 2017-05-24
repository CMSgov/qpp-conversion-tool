#!/bin/bash
FILES=$(dirname $0)/publicKeys/*
for f in $FILES
do
  echo "Processing $f file..."
  USER=$(basename $f)
  sudo -s -- <<EOF
  useradd -m -p $(openssl passwd -crypt 1234) -s /bin/bash $USER
  usermod -aG sudo $USER
  mkdir /home/$USER/.ssh
  cp $f /home/$USER/.ssh/id_rsa.pub
  cp $f /home/$USER/.ssh/authorized_keys
  chown -R $USER:$USER /home/$USER/.ssh
EOF
done