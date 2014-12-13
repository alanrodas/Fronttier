import sbt._

organization := Fronttier.organization

name := Fronttier.name("cli")

version := Fronttier.version

crossScalaVersions := Fronttier.crossScalaVersions

resolvers += "alanrodas" at "http://alanrodas.com/maven/releases"

libraryDependencies ++= Seq(
  "com.alanrodas" %% "scaland-cli" % "0.1"
)