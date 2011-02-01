#!/bin/bash
rm -rf /tmp/substorage
rm -f /tmp/image_*.png
java -cp target/videosaic-1.0-SNAPSHOT-jar-with-dependencies.jar nl.bneijt.videosaic.App "$@"
ffmpeg -y -f image2 -i /tmp/image_%05d.png -s 640x480 -r 12 /tmp/image.avi &&
gnome-open /tmp/image.avi

