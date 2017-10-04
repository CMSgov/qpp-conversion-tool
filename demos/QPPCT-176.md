# QPPCT-176

`git checkout 678a8c25e3667987d4a68763bfe851daea6e64bf`

## Set-up
1. Add invocations of the `DbService` to the controller.
1. Have a DynamoDB table initialized.

## Steps
1. Show the JIRA. https://jira.cms.gov/browse/QPPCT-176
1. Set the `DYNAMO_TABLE_NAME` to the name of the table in DynamoDB.
1. Run the ReST API.
1. Invoke the ReST API.
1. View the DynamoDB table and see the new item.