#!/usr/bin/env bash


function build_and_tar_converter_image() {
	echo "Build the QPP Conversion Tools Docker image"
	docker build -t qpp_conversion .

	echo "Export the QPP Conversion Tools Docker image as a tar file"
	docker save -o qpp_conversion_docker_image.tar qpp_conversion
}

function run_ansible() {
	echo "Running the Ansible playbook"
	ansible-playbook -i "$1," --extra-vars "remote_username=$2 sudoer_password=$3" ./ansible/conversion_playbook.yml
}

HOSTS=$1
REMOTE_USERNAME=$2
SUDOER_PASSWORD=$3

build_and_tar_converter_image
run_ansible ${HOSTS} ${REMOTE_USERNAME} ${SUDOER_PASSWORD}

echo "Done!"
