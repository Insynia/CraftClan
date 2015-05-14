#!/bin/sh

if [ -z "$DB_USERNAME" ]; then
    echo "/!\\ YOU MUST SET DB_USERNAME /!\\"
    exit
fi
if [ -z "$DB_URL" ]; then
    echo "/!\\ YOU MUST SET DB_URL /!\\"
    exit
fi
if [ -z "$DB_PASSWORD" ]; then
    echo "/!\\ YOU MUST SET DB_PASSWORD /!\\"
    exit
fi

SCREEN_NAME='minecraft'
screen -AmS $SCREEN_NAME java -Xms512M -Xmx1024M -XX:MaxPermSize=128M -jar spigot.jar
