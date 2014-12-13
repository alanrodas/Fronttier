organization := Fronttier.organization

name := Fronttier.name

version := Fronttier.version

crossScalaVersions := Fronttier.crossScalaVersions

libraryDependencies ++= Seq(
	"org.scala-lang.modules" % "scala-parser-combinators_2.11" % "1.0.2",
	"org.scala-lang.modules" % "scala-xml_2.11" % "1.0.2",
	"com.propensive" %% "rapture-io" % "0.9.1",
	"com.propensive" %% "rapture-fs" % "0.9.1",
	"com.propensive" %% "rapture-net" % "0.9.0",
	"com.typesafe" % "config" % "1.2.1"
)

resolvers += "alanrodas" at "http://alanrodas.com/maven/releases"

libraryDependencies ++= Seq(
	"com.alanrodas" %% "scaland" % "0.1",
	"com.typesafe.scala-logging" %% "scala-logging-api" % "2.1.2",
	"com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
	"ch.qos.logback" % "logback-classic" % "1.1.2"
)