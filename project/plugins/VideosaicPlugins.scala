import sbt._

class VideosaicPlugins(info: ProjectInfo) extends PluginDefinition(info)
{
    val eclipsify = "de.element34" % "sbt-eclipsify" % "0.6.0"
}
