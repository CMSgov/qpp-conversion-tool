#!/usr/bin/env bash
BASE_URL=https://api.github.com/repos/$CIRCLE_PROJECT_USERNAME/$CIRCLE_PROJECT_REPONAME;
STATUS_UPDATE=$BASE_URL/statuses/$CIRCLE_SHA1;

function sendStatus {
    curl -H "Content-Type: application/json" -H "Authorization: token $BUILD_STATUS_TOKEN" \
        -d "$PAYLOAD" -X POST $STATUS_UPDATE;
}

function makePayload {
    PAYLOAD=$(jq --arg key0   'state' \
        --arg value0 'success' \
        --arg key1   'target_url' \
        --arg value1 "https://circleci.com/workflow-run/$CIRCLE_WORKFLOW_WORKSPACE_ID" \
        --arg key2   'description' \
        --arg value2 "The build was a success!" \
        --arg key3   'context' \
        --arg value3 'ci/circleci' \
        '. | .[$key0]=$value0 | .[$key1]=$value1 | .[$key2]=$value2 | .[$key3]=$value3' \
        <<<'{}');
}

makePayload
sendStatus