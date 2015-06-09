#!/bin/sh

if [ -z "$SCREEN_NAME" ]; then
    echo "/!\\ SCREEN_NAME IS NOT SET /!\\"
    exit 1
fi

echo "Allowing IP <`dig +short craftclan.fr`> to request the server"
CRAFTCLAN_WEB_IP=`dig +short craftclan.fr` screen -AmdS craftclan-listener bundle exec thin start -e production