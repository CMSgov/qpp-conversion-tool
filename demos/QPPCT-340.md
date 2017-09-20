# QPPCT-340

`git checkout a8827a288542d77e8f26f6031085096e3cd4e786`

## Changes
- Create a DynamoDB service that extends the `AnyOrderAsyncActionService`.
- Ensure there is a long sleep in `asynchronousAction` to demonstrate its asynchronous nature.

## Steps
1. Show Jira.  https://jira.cms.gov/browse/QPPCT-340
1. Show empty DynamoDB table.
1. Invoke ReST API on `Profile testing` endpoint.
   - Notice that the response comes back quickly but the write to DynamoDB has not completed yet.
1. Show DynamoDB table and its written item.
1. Run ReST API locally.
1. Invoke local ReST API endpoint.
1. Show the log that it retries with increasing waits.
