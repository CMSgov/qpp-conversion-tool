#!/usr/bin/env bash

docker build -t qpp_ansible .
docker run --rm -d -p 2266:22 qpp_ansible

ssh-copy-id root@localhost -p 2266
ansible-playbook -i ./host-inventory ./playbook.yml
