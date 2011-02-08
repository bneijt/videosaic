#!/bin/bash
#TODO Create mavne task for this.
mvn assembly:assembly && 
rm /tmp/image_*.png
java -cp target/videosaic-1.0-SNAPSHOT-jar-with-dependencies.jar nl.bneijt.videosaic.SubFrameClient localhost:8080 "$@" &&
ffmpeg -y -f image2 -r 25 -i /tmp/image_%05d.png -s 640x480 -r 25 /tmp/image.ogg &&
gnome-open /tmp/image.ogg

