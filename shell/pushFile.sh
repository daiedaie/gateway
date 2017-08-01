#!/bin/bash

ip=$1
username=$2
password=$3
#Fpath=$4
Lpath=$4
filename=$5

ftp -i -n <<FTPIT
open $ip
user $username $password
A
bin
put $Lpath $filename
ls $filename
quit
FTPIT

exit 0
