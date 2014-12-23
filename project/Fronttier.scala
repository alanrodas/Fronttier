import java.io.File

import sbt._

object Fronttier {
  lazy val organization = "com.alanrodas"
  lazy val version = "0.2"
  lazy val scalaVersion = "2.11.4"
  lazy val crossScalaVersions = Seq("2.10.4","2.11.4")
  lazy val name = "fronttier"
  def name(name : String) = "fronttier-" + name

  def dependencies = Seq(
    "org.scalatest" %% "scalatest" % "2.2.1" % "test"
  )

  private def getFileResolver(location : String) : Option[Resolver] = {
    Some(Resolver.file("file",
      new File("../alanrodas.github.io/maven/" + location)))
  }

  val publishLocation = {(v: String) =>
    if (v.trim.endsWith("SNAPSHOT")) {getFileResolver("snapshots")}
    else {getFileResolver("releases")}
  }
}