import sbt._

organization := Fronttier.organization

name := "sbt-fronttier"

version := Fronttier.version

scalaVersion := "2.10.4"

sbtVersion := "0.13.5"

sbtPlugin := true

addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.0.0")