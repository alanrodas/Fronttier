organization := Fronttier.organization

name := Fronttier.name("cli")

version := Fronttier.version

scalaVersion := Fronttier.scalaVersion

crossScalaVersions := Fronttier.crossScalaVersions

publishTo <<= version {Fronttier.publishLocation}

resolvers += "alanrodas" at "http://alanrodas.com/maven/releases"

libraryDependencies ++= Seq(
  "com.alanrodas" %% "scaland-cli" % "0.2"
)