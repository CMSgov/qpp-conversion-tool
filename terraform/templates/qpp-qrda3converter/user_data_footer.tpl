#!/bin/sh

chmod 644 /etc/profile.d/corevpc.sh

source /etc/profile

service coreenv_config_id restart
service coreenv_config_newrelic restart

chkconfig --add coreenv_config_hostnames
chkconfig coreenv_config_hostnames on
