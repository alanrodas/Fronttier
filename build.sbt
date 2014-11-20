organization := "com.alanrodas"

name := "fronttier"

version := "0.1"

scalaVersion := "2.11.2"

sbtVersion := "0.13.6"

sbtPlugin := true

resolvers += "fwbrasil" at "http://fwbrasil.net/maven"

libraryDependencies ++= Seq(
	"org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.2",
	"org.scala-lang.modules" %% "scala-xml" % "1.0.2",
	"com.propensive" %% "rapture-io" % "0.9.1",
	"com.propensive" %% "rapture-fs" % "0.9.1",
	"com.propensive" %% "rapture-net" % "0.9.0"
)