#!/bin/sh

echo "Allowing IP <`dig +short craftclan.fr`> to request the server"
CRAFTCLAN_WEB_IP=`dig +short craftclan.fr` bundle exec thin start -e production