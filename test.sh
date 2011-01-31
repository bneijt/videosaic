#!/bin/bash
mvn clean assembly:assembly
java -cp target/videosaic-1.0-SNAPSHOT-jar-with-dependencies.jar nl.bneijt.videosaic.App clean
rm -rf /tmp/substorage
rm -f /tmp/image_*.png
echo "##################### SUPER"
java -cp target/videosaic-1.0-SNAPSHOT-jar-with-dependencies.jar nl.bneijt.videosaic.App super vid/onesecond.ogg &&
echo "##################### SUB" &&
java -cp target/videosaic-1.0-SNAPSHOT-jar-with-dependencies.jar nl.bneijt.videosaic.App sub vid/plane.ogg &&
echo "##################### COLLAPSE" &&
java -cp target/videosaic-1.0-SNAPSHOT-jar-with-dependencies.jar nl.bneijt.videosaic.App collapse vid/onesecond.ogg &&
echo "##################### SUPER " &&
ffmpeg -y -f image2 -i /tmp/image_%d.png -s 640x480 -r 12 /tmp/image.avi &&
gnome-open /tmp/image.avi

