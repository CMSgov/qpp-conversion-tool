#!/bin/sh

cat > /etc/profile.d/corevpc.sh <<EOF
export COREVPC_NAME="${vpc_name}" \\
  COREVPC_S3_SHARED_URL="s3://${s3_bucket}/shared" \\
  COREVPC_S3_URL="s3://${s3_bucket}/${vpc_name}" \\
  COREVPC_FQDN="${vpc_name}.hcgov.internal"
EOF
