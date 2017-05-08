#!/usr/bin/env bash

if [[ "$CIRCLE_BRANCH" == "master" || ( ! -z $SERVERLESS_OTHER_BRANCH && "$CIRCLE_BRANCH" == "$SERVERLESS_OTHER_BRANCH" ) ]]; then
	serverless deploy
else
	echo "Not on master so not deploying lambda function to AWS."
fi