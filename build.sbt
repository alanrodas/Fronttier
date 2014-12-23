organization := Fronttier.organization

name := Fronttier.name("parent")

version := Fronttier.version

scalaVersion := Fronttier.scalaVersion

crossScalaVersions := Fronttier.crossScalaVersions

publishTo <<= version {Fronttier.publishLocation}

lazy val core = project

lazy val cli = project dependsOn core

lazy val sbt_plugin = project dependsOn core

lazy val play_plugin = project dependsOn core