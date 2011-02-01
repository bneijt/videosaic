#!/bin/bash
mvn clean assembly:assembly
rm -rf /tmp/substorage
rm -f /tmp/image_*.png
echo "##################### SUB" &&
java -cp target/videosaic-1.0-SNAPSHOT-jar-with-dependencies.jar nl.bneijt.videosaic.App sub vid/plane.ogg &&
echo "##################### SUPER"
java -cp target/videosaic-1.0-SNAPSHOT-jar-with-dependencies.jar nl.bneijt.videosaic.App super vid/onesecond.ogg &&
ffmpeg -y -f image2 -i /tmp/image_%d.png -s 640x480 -r 12 /tmp/image.avi &&
gnome-open /tmp/image.avi

