#!/bin/bash

echo $1 >> /etc/vsftpd/gateway_vuser.txt
echo $2 >> /etc/vsftpd/gateway_vuser.txt

db_load -T -t hash -f /etc/vsftpd/gateway_vuser.txt /etc/vsftpd/gateway_vuser.db

cp /etc/vsftpd/vuser_conf/initSetting /etc/vsftpd/vuser_conf/$1

echo local_root=/home/gateway/vuser/$3/$1 >> /etc/vsftpd/vuser_conf/$1

mkdir -p /home/gateway/vuser/$3/$1
