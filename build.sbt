organization := Fronttier.organization

name := Fronttier.name("parent")

version := Fronttier.version

crossScalaVersions := Fronttier.crossScalaVersions

lazy val core = project

lazy val cli = project dependsOn core

lazy val sbt_plugin = project dependsOn core