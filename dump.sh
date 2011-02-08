#!/bin/bash
#TODO Create mavne task for this.
mvn assembly:assembly && 
rm /tmp/image_*.png
java -cp target/videosaic-1.0-SNAPSHOT-jar-with-dependencies.jar nl.bneijt.videosaic.SubFrameFileDumper "$@"

