# QPPCT-338

`git checkout 11acac2ba5338a5b13f9636c78120cc3ffa11a52`

## Prerequisites
- Remove the `alarms` sub-layer from `s3ForPii.js`.
- Remove the parameter values from `cloudfrom` in the CoreVPC repository.
- PWD is in this repository.
- AWS MFA is active.

## Steps
1. Show Jira.  https://jira.cms.gov/browse/QPPCT-338
1. `$ cloudform ./vpcs/flexion/qpp-conversion-tool-test.js`
1. Upon completion, log into AWS.
1. Show S3 bucket is created
1. Show S3 bucket's policy and outline the encryption and role requirement.
