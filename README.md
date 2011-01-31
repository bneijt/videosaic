Mosaic program for video's
==========================

Future usage:
- Input a super video into the MongoDB instance using the "super" command
- Input a sub video into the MongoDB and on disk using the "sub" command
- Collapse the video's into one using MongoDB and the disk stored images

It should create a video based on the different video's you've given to create a mosaic.
There will be NO video changes to change the tone or other information.


Plan
----
- Create "sub" frames by analysing the target video and placing an identity
  of the frame in a MongoDB
- Run through all "source" frames and look their identity up in the database
  as well. If a match is found, note it in the MongoDB document of the target
  frame.
- Compile the source frames into the result video by going through the target
  frames and loading the accompanying source frames for each one.

TODO
----
- Add database matching system (MongoDB map-reduce job?)
- Remove all constants
- Add code to generate a video without using ffmpeg
- Find the beste matching system
- Do a complete rewrite

Current usage:
((Make sure you have mongodb running))
mvn clean assembly:assembly
java -cp target/videosaic-1.0-SNAPSHOT-jar-with-dependencies.jar nl.bneijt.videosaic.App super vid/onesecond.ogg
java -cp target/videosaic-1.0-SNAPSHOT-jar-with-dependencies.jar nl.bneijt.videosaic.App sub vid/plane.ogg
java -cp target/videosaic-1.0-SNAPSHOT-jar-with-dependencies.jar nl.bneijt.videosaic.App collapse
ffmpeg -f image2 -i /tmp/image_%d.png -s 640x480 -r 12 /tmp/image.avi

