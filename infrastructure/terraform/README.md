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
