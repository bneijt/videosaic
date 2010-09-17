Mosaic program for video's
==========================

Input one target video, input multiple source video's (may include the target video)

It should create a video based on the different video's you've given to create a mosaic.
There will be NO video changes to change the tone or other information.

After checkout
--------------
- sbt update
- sbt compile

Plan
----
- Create "target" frames by analysing the target video and placing an identity
  of the frame in a MongoDB
- Run through all "source" frames and look their identity up in the database
  as well. If a match is found, note it in the MongoDB document of the target
  frame.
- Compile the source frames into the result video by going through the target
  frames and loading the accompanying source frames for each one.

TODO
----
- Add commandline argument parser
- Encapsulate FrameGenerator into an scala Actor OR change it
  into a java.nio Filter, or java.awt.image Producer interface.
- Add database backend for image result storage
- Add datbase matching system
- Create image filter to extract Frame information


