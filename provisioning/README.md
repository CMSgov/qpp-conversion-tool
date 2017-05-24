The script depends on the [docker-machine](https://docs.docker.com/machine/) utility.

An envVars.sh script must be included in the `provisioning`
directory that specifies the following.

```
#!/bin/bash
export ACCESS_KEY=[user's access key]
export SECRET_KEY=[user's secret key]

export VPC_ID=[id of vpc in which the ec2 instance will be created]
export SUBNET_ID=[id of subnet in which the ec2 instance will be created]
```

Usage example:

./provision.sh [name of machine]

./provision.sh machineName
