# QPPCT-176

`git checkout 678a8c25e3667987d4a68763bfe851daea6e64bf`

## Set-up
1. Add invocations of the `DbService` to the controller.
1. Have a DynamoDB table initialized.

## Steps
1. Show the JIRA. https://jira.cms.gov/browse/QPPCT-176
1. Have no environment variables set.
   1. Run the ReST API.  It will fail due to `KMS_KEY` being unspecified.
1. Have the `DYNAMO_TABLE_NAME` environment variables set.
   1. Run the ReST API.  It will fail due to `KMS_KEY` being unspecified.
1. Have both the `DYNAMO_TABLE_NAME` and `KMS_KEY` environment variables set.
   1. Run the ReST API.  It will work!  Yay!
   1. Invoke the ReST API.
   1. Show the DynamoDB.
      1. Notice that a new item is added.
      1. The UUID is auto-generated.
      1. The CreateTime is auto-generated.
      1. The TIN is encrypted.
      1. Everything else is unencrypted.
1. Have the `NO_AUDIT` environment variable set but `DYNAMO_TABLE_NAME` and `KMS_KEY` _not_ set.
   1. Run the ReST API.  It will work even though the `KMS_KEY` is not set.
   1. Invoke the Rest API.
   1. Notice that no new item is added to DynamoDB because `NO_AUDIT` is set.
