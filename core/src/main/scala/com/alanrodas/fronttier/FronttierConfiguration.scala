/*
 * Copyright 2014 Alan Rodas Bonjour
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package com.alanrodas.fronttier

import com.alanrodas.fronttier.parsers._
import com.alanrodas.fronttier.io._
import rapture.fs._
import platform.adaptive
import com.typesafe.config._
import com.alanrodas.scaland.logging._

case class FronttierDefaults(useCache : Option[Boolean], force : Option[Boolean],
                             verbose : Option[Boolean], destination : Option[String]) {
  def overritenBy(defaults : FronttierDefaults) = {
    FronttierDefaults(
      useCache = if (defaults.useCache.isDefined) defaults.useCache else useCache,
      force = if (defaults.force.isDefined) defaults.force else force,
      verbose = if (defaults.verbose.isDefined) defaults.verbose else verbose,
      destination = if (defaults.destination.isDefined) defaults.destination else destination
    )
  }
  override def toString = {
    "Default values:" +
        "\n  verbose is " + verbose.getOrElse("not defined") +
        "\n  force is " + force.getOrElse("not defined") +
        "\n  use cache is " + useCache.getOrElse("not defined") +
        "\n  destination is " + destination.getOrElse("not defined")
  }
  def toConfig = {
    FronttierConfiguration(
      useCache = useCache.getOrElse(true),
      verbose = verbose.getOrElse(false),
      force = force.getOrElse(false),
      destination = destination.fold(File.currentDir){dest => dest.asFile}
    )
  }
}

object FronttierDefaults {

  val UserFttRcFile = File.home / ".fttrc"
  val ProjectFttRcFile = File.currentDir / ".fttrc"

  lazy val defaults : FronttierDefaults = {
    default() overritenBy
        FronttierDefaults.fromFile(UserFttRcFile) overritenBy
        FronttierDefaults.fromFile(ProjectFttRcFile)
  }

  private def default() = {
    FronttierDefaults(
      useCache = Some(true),
      force = Some(false),
      verbose  = Some(false),
      destination = Some(File.currentDir.pathString)
    )
  }

  private def empty() = {
    FronttierDefaults(
      useCache = None,
      force = None,
      verbose  = None,
      destination = None
    )
  }

  def fromFile(path : FileUrl) = {
    if (!path.exists) {
      empty()
    } else {
      val conf = ConfigFactory.parseFile(path.javaFile)
      val verbose = if (conf.hasPath("verbose"))
                      Some(conf.getBoolean("verbose")) else None
      val force = if (conf.hasPath("force"))
                    Some(conf.getBoolean("force")) else None
      val usecache = if (conf.hasPath("use-cache"))
                       Some(conf.getBoolean("use-cache")) else None
      val destination = if (conf.hasPath("destination"))
                          Some(conf.getString("destination")) else None
      FronttierDefaults(
        useCache = usecache,
        force = force,
        verbose = verbose,
        destination = destination)
    }
  }
}

case class FronttierConfiguration(useCache : Boolean, force : Boolean,
    verbose : Boolean, destination : FileUrl) {
  val cache : Cache = if (useCache) Cache() else NoCache
  def configureLogger(logger : Logger): FronttierConfiguration = {
    logger.level = if (verbose) Info else Warn
    this
  }
}

object FronttierConfiguration {
  def apply(useCache : Boolean, force : Boolean, verbose : Boolean, destination : String) : FronttierConfiguration = {
    FronttierConfiguration(useCache, force, verbose, destination.toFile)
  }
}