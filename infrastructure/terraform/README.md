#
Pre-req:
bucket must already created, you must have appropirate AWS environment variables exported.
```bash
aws s3api create-bucket --bucket <bucket-name-holding-tf-state> --acl private --profile <AWS_PROFILE>
```

The following should be exported:
```bash
AWS_SESSION_TOKEN
AWS_DEFAULT_REGION
AWS_SECRET_ACCESS_KEY
AWS_ACCESS_KEY_ID
```

The Converstion Tool Docker container requires a few dozen parameters passed to it on boot, look in the SSM terraform, S3 buckets and dynamodb:

##S3 Buckets
### Parameter buckets (legacy - the parameters are now stored in SSM)
aws-hhs-cms-ccsq-qpp-navadevops-prod-us-east-1
  /qpp-qrda3converter-prod
aws-hhs-cms-ccsq-qpp-navadevops-nonprod-us-east-1
  /qpp-qrda3converter-{env} (All non production environments: dev, impl, val)
### Application Data
  stores encrypted QRDA-III and QPP json files
  Bucket: aws-hhs-cms-ccsq-qpp-navadevops-pii-convrtr-audt-*$ENV*-us-east-1
  Key:    qpp-qrda3converter-*$ENV*-kms_alias
### encrypted PII buckets that holds TIN/NPI/APM validation list
   Bucket: aws-hhs-cms-ccsq-qpp-navadevops-pii-cnvrt-npicpc-*$ENV*-us-east-1
   Key: qpp-qrda3converter-*$ENV*-cpc-plus-kms_alias
##DynamoDb
All tables include DynamoDB encryption context with client-side encryption of values.
   Table: qpp-qrda3converter-$ENV-metadata
   Key: qpp-qrda3converter-$ENV-kms_alias
Back up:
- Dynamodb has backups ran via jenkins that backup data. Is schedule to run once a day and holds up to 5 backups.

TODO: Bring in S3 & Dynamo tables. I think we can get rid of the parameter buckets.
