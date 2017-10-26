#!/usr/bin/env bash
BASE_URL=https://api.github.com/repos/$CIRCLE_PROJECT_USERNAME/$CIRCLE_PROJECT_REPONAME
STATUS_UPDATE=$BASE_URL/statuses/$CIRCLE_SHA1
STATUS_URL=$BASE_URL/status/$CIRCLE_SHA1;
curl $STATUS_URL -o status.json
STATUS=$(cat status.json | jq '.state' | tr -d '"')

PAYLOAD=$(jq --arg key0   'state' \
   --arg value0 "$STATUS" \
   --arg key1   'target_url' \
   --arg value1 "https://circleci.com/workflow-run/$CIRCLE_WORKFLOW_WORKSPACE_ID" \
   --arg key2   'description' \
   --arg value2 "The build resulted in $STATUS" \
   --arg key3   'context' \
   --arg value3 'ci/circleci' \
   '. | .[$key0]=$value0 | .[$key1]=$value1 | .[$key2]=$value2 | .[$key3]=$value3' \
   <<<'{}');

curl -H "Content-Type: application/json" -H "Authorization: token $BUILD_STATUS_TOKEN" \
   -d $PAYLOAD -X POST $STATUS_UPDATE