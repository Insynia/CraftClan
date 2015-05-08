#!/bin/sh

cd server >& /dev/null || mkdir server && cd server

# Packages install

sudo apt-get update
sudo apt-get install emacs24-nox -y
sudo apt-get install openjdk-7-jdk -y
sudo apt-get install git -y
sudo apt-get install tar -y

# Build mc server

curl https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar -o BuildTools.jar
git config --global --unset core.autocrlf
java -jar BuildTools.jar
mv spigot*.jar spigot.jar
cd server >& /dev/null || mkdir server && cd server
mv ../spigot.jar .

# Launch script

echo "#!/bin/sh\n\njava -Xms512M -Xmx1024M -XX:MaxPermSize=128M -jar spigot.jar" > start.sh
chmod +x start.sh

# License agreement

echo "eula=true" > eula.txt
