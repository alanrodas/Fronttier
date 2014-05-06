ideaExcludeFolders += ".idea"

ideaExcludeFolders += ".idea_modules"

name := "fronttier"

version := "0.1"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
	"com.propensive" %% "rapture-io" % "0.9.1",
	"com.propensive" %% "rapture-fs" % "0.9.1",
	"com.propensive" %% "rapture-net" % "0.9.0",
	"org.tmatesoft.svnkit" % "svnkit" % "1.8.3"
)

libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.5" % "test"