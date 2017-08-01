#!/bin/bash

useradd -d /home/gateway/users/$1 -m $1
su - $1 -c "mkdir result"
