organization := Fronttier.organization

name := "play-fronttier"

version := Fronttier.version

scalaVersion := Fronttier.scalaVersion

crossScalaVersions := Fronttier.crossScalaVersions

publishTo <<= version {Fronttier.publishLocation}

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
	"com.typesafe.play" %% "play" % "2.3.6"
)