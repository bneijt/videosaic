#!/bin/bash
#TODO Create mavne task for this.
mvn assembly:assembly && 
java -cp target/videosaic-1.0-SNAPSHOT-jar-with-dependencies.jar nl.bneijt.videosaic.SubFrameServer "$@"

