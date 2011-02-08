#!/bin/bash
mvn assembly:assembly
java -cp target/videosaic-1.0-SNAPSHOT-jar-with-dependencies.jar nl.bneijt.videosaic.SubFrameServer vid/onesecond.ogg &
SERVER=$!
echo "Sleeping 10 seconds for server load data"
sleep 5
echo "Starting client"
#Called as: host host host video
java -cp target/videosaic-1.0-SNAPSHOT-jar-with-dependencies.jar nl.bneijt.videosaic.SubFrameClient localhost:8080 vid/onesecond.ogg &&
echo "Killing server"
kill $SERVER
echo "Running ffmpeg to collapse images into video"
ffmpeg -y -f image2 -r 12 -i /tmp/image_%05d.png -s 640x480 -r 25 /tmp/image.ogg


#mvn clean assembly:assembly
#rm -rf /tmp/substorage
#rm -f /tmp/image_*.png
#java -cp target/videosaic-1.0-SNAPSHOT-jar-with-dependencies.jar nl.bneijt.videosaic.App -s vid/colors.ogg -S vid/onesecond.ogg | tee output.log &&
#gnome-open /tmp/image.ogg

