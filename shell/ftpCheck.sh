#!/bin/bash

ftp -i -n $1 <<FTPEOF
user $2 $3

FTPEOF

