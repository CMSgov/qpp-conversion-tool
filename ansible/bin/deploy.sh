#!/usr/bin/env bash

DIRECTORY=$(dirname $0)
DEPLOY_LOCATION=$1
SUDO_PASSWORD=$2

echo 'Retrieving resources'
exec $DIRECTORY/getSource.sh

echo "Deploying to $DEPLOY_LOCATION"

if [[ "$DEPLOY_LOCATION" == "docker" ]]; then
	# stop any currently running containers that are running the qpp_ansible image
	RUNNING_CONTAINERS=$(docker ps  -q --filter "ancestor=qpp_ansible")
	for CONTAINER in ${RUNNING_CONTAINERS}
	do
		echo "Killing container $CONTAINER because it is running the qpq_ansible image"
		docker kill ${CONTAINER}
		sleep 1
	done

	echo "Deleting the qpp_ansible image"
	docker rmi qpp_ansible

	echo "Building the qpp_ansible image"
	docker build -t qpp_ansible .
	echo "Running the qpp_ansible image"
	docker run --rm -d -p 2266:22 -p 8066:8080 qpp_ansible

	# This is so the user doesn't get the SSH MITM warning
	echo "Removing the previous known host line for the container"
	sed -i '' -E "/\[localhost\]:2266/d" ~/.ssh/known_hosts

	echo "Loading your public key into the container"
	echo "The password to the container for root is ansible"
	ssh-copy-id root@localhost -p 2266

	echo "Running the Ansible playbook"
	ansible-playbook -i ./host-inventory ./docker_playbook.yml

	echo "Done!"
	echo "ssh root@localhost -p 2266"
elif [[ "$DEPLOY_LOCATION" == "ec2" ]]; then
	echo "Running the Ansible playbook"
	ansible-playbook --ask-become-pass -i ./host-inventory ./ec2_playbook.yml

	echo "Done!"
fi
