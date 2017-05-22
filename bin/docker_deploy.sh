#!/usr/bin/env bash


function build_and_tar_converter_image() {
	echo "Build the QPP Conversion Tools Docker image"
	docker build -t qpp_conversion .

	echo "Export the QPP Conversion Tools Docker image as a tar file"
	docker save -o qpp_conversion_docker_image.tar qpp_conversion
}

function run_ansible() {
	echo "Running the Ansible playbook"
	ansible-playbook --ask-become-pass -i ./ansible/host-inventory --extra-vars "host_category=$1 remote_username=$2" ./ansible/conversion_playbook.yml
}

build_and_tar_converter_image
run_ansible ec2 pkendall

echo "Done!"
