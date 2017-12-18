#!/bin/bash

function build_and_tar_converter_image() {
	echo "Build the QPP Conversion Tools Docker image"
	docker build -t qpp_conversion .

	echo "Export the QPP Conversion Tools Docker image as a tar file"
	docker save -o qpp_conversion_docker_image.tar qpp_conversion
}

function run_ansible() {
	echo "Running the Ansible playbook"
	ANSIBLE_HOST_KEY_CHECKING=False ansible-playbook -i "$1," --extra-vars "remote_username=$2 sudoer_password=$3" ./tools/ansible/conversion_playbook.yml
}

#if [[ "$CIRCLE_BRANCH" == "master" || ( ! -z $DOCKER_DEPLOY_OTHER_BRANCH && "$CIRCLE_BRANCH" == "$DOCKER_DEPLOY_OTHER_BRANCH" ) ]]; then

    sudo su
    apt-get update
    export RUNLEVEL=1
    apt-get install apt-transport-https ca-certificates curl gnupg2 software-properties-common
    curl -fsSL https://download.docker.com/linux/$(. /etc/os-release; echo "$ID")/gpg | apt-key add -
    apt-key fingerprint 0EBFCD88
    add-apt-repository    "deb [arch=amd64] https://download.docker.com/linux/$(. /etc/os-release; echo "$ID") \
    $(lsb_release -cs) stable"
    apt-get update
    apt-get install docker-ce

	build_and_tar_converter_image
	run_ansible ${DOCKER_DEPLOY_HOSTS} ${DOCKER_DEPLOY_REMOTE_USERNAME} ${DOCKER_DEPLOY_SUDOER_PASSWORD}

	echo "Done!"
#else
#	echo "Not on master so not deploying Docker image."
#fi
