#!/bin/bash

userName=\"$1\"
passwd=\"$2\"
value1="\$0==$userName"

command="awk '{if(a){print $passwd}else print;if($value1){a=1}else{a=0}}' /etc/vsftpd/gateway_vuser.txt > /home/gateway/shell/temp.txt"

`eval $command`

cp /home/gateway/shell/temp.txt /etc/vsftpd/gateway_vuser.txt

db_load -T -t hash -f /etc/vsftpd/gateway_vuser.txt /etc/vsftpd/gateway_vuser.db
