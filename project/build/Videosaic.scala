import sbt._

class Videosaic(info: ProjectInfo) extends DefaultProject(info)
{
    val dep = "com.googlecode.gstreamer-java" % "gstreamer-java" % "1.4"
    val monbodb = "org.mongodb" % "mongo-java-driver" % "2.1"
}
