import sbt._
import de.element34.sbteclipsify._

class Videosaic(info: ProjectInfo) extends DefaultProject(info) with Eclipsify {
    val dep = "com.googlecode.gstreamer-java" % "gstreamer-java" % "1.4"
    val monbodb = "org.mongodb" % "mongo-java-driver" % "2.1"
    val cli = "commons-cli" % "commons-cli" % "1.2"
}
