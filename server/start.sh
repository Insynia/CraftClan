#!/bin/sh

PATH_LISTENER='../craftclan-listener/'

if [ -z "$DB_USERNAME" ]; then
    echo "/!\\ YOU MUST SET DB_USERNAME /!\\"
    exit 1
fi
if [ -z "$DB_URL" ]; then
    echo "/!\\ YOU MUST SET DB_URL /!\\"
    exit 1
fi
if [ -z "$DB_PASSWORD" ]; then
    echo "/!\\ YOU MUST SET DB_PASSWORD /!\\"
    exit 1
fi

export SCREEN_NAME='minecraft'
cd $PATH_LISTENER
./start.sh

if [ $? -eq 0 ]; then
    echo "The command listener launched successfully"
else
    echo "The command listener failed to launch"
    exit 1
fi

cd -

mkdir -p structures
screen -AmS $SCREEN_NAME java -Xms512M -Xmx1024M -XX:MaxPermSize=128M -jar spigot.jar
