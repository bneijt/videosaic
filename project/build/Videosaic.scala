import sbt._

class Videosaic(info: ProjectInfo) extends DefaultProject(info)
{
    val dep = "com.googlecode.gstreamer-java" % "gstreamer-java" % "1.4"
}
