To build a new base image:

```
export APP_BASE_DIR=`pwd`
../corevpc/tools/build-amis.sh base base us-east-1 --var-file `pwd`/vpcs/packer-common.json --var-file `pwd`/vpcs/packer-base-gdit-july.json
```
