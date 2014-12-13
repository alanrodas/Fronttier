/** ********************************************************************************************
  * Testing
  * Version 0.1
  *
  * The primary distribution site is
  *
  * http://scalavcs.alanrodas.com
  *
  * Copyright 2014 alanrodas
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
  * *********************************************************************************************/
package com.alanrodas.fronttier

import com.alanrodas.fronttier.parsers._
import com.alanrodas.fronttier.io._
import rapture.fs._
import platform.adaptive
import com.typesafe.config._
import rapture.core.strategy.throwExceptions
import com.typesafe.scalalogging.slf4j.Logger

import com.typesafe.scalalogging.slf4j.LazyLogging


class Fronttier(val workingDir : FileUrl, val destination : FileUrl, val parsers : Seq[ConfigParser],
    val configuration : FronttierConfiguration) extends LazyLogging {

  configuration.configureLogger(logger)

  val database = FronttierDatabase(destination)

  val configParser = parserFileAt(workingDir)

  require(configParser.nonEmpty, s"Could not find a configuration file at: $workingDir." +
      " Create a fronttier.xml or fronttier.ftt file at that location")

  def configureLogger(logger : Logger): Unit = {
    configuration.configureLogger(logger)
  }

  lazy val rootDependency = {
    val parser = configParser.get
    logger.info("Found " + parser.name + " file by the name " + parser.fileName + " at " + workingDir.pathString)
    parser.parseAt(workingDir)
  }

  private def beforeDownloadRoot(): Unit = {
    if (configuration.force) {
      logger.info("===============")
      logger.info("Forcing enabled, deleting old files")
      deleteAll()
      logger.info("===============")
    }
  }

  private def beforeDownload(dependency : Dependency): Unit = {
    if (configuration.force) {
      logger.info("Forcing enabled, deleting old files")
      delete(dependency)
    }
  }

  private def afterDownload(maybeConfig : Option[Configuration], errors : Seq[FronttierException]): Unit = {
    if (maybeConfig.isDefined) {database.save(maybeConfig.get)}
    else {for (error <- errors) logger.error(error.getMessage)}
  }

  private def afterDownloadRoot(config : Configuration, errors : Seq[FronttierException]): Unit = {
    logger.info("Saving information to the database")
    database.save(config)
    logger.info("")
    logger.info("===========================")
    logger.info("")
    if (errors.isEmpty) {
      logger.setAsInfo
      logger.info("Fronttier finalized successfuly")
    } else {
      logger.error("There were errors while downloading the dependencies:")
      for(error <- errors) {
        logger.error(error.getMessage)
      }
    }
  }

  def downloadRoot() : Seq[FronttierException] = {
    beforeDownloadRoot()
    val rootDep = rootDependency
    logger.info("===============")
    logger.info("Cache is " + (cache match {
      case LocationCache(path) => "located at " + path.pathString
      case NoCache => "not used"
    }))
    logger.info("===============")

    if (!isInstalled(rootDep.currentDependency)) {
      val (config, errors) = rootDep.download(destination)(this)
      afterDownloadRoot(config, errors)
      errors
    } else {
      logger.warn("The dependency " + rootDep.currentDependency + " is already installed.")
      logger.warn("Nothing to do.")
      Nil
    }
  }

  def download(dependency : Dependency, repositories : Seq[Repository] = Repository.defaults): Seq[FronttierException] = {
    beforeDownload(dependency)
    logger.info("===============")
    logger.info("Cache is " + (cache match {
      case LocationCache(path) => "located at " + path.pathString
      case NoCache => "not used"
    }))
    logger.info("===============")

    if (!isInstalled(dependency)) {
      val (config, errors) = dependency.download(destination, repositories)(this)
      afterDownload(config, errors)
      errors
    } else {
      logger.warn("The dependency " + dependency + " is already installed.")
      logger.warn("Nothing to do.")
      Nil
    }
  }

  def download(dependency : Configuration): Seq[FronttierException] = {
    beforeDownload(dependency.currentDependency)
    cache.save(dependency, destination)(this)
    if (dependency.dependencies.nonEmpty) {
      if (!isInstalled(dependency.currentDependency)) {
        val (config, errors) = dependency.download(destination)(this)
        afterDownload(Some(config), errors)
        errors
      } else {
        logger.warn("The dependency " + dependency + " is already installed.")
        logger.warn("Nothing to do.")
        Nil
      }
    } else Nil
  }

  def delete(dependency : Dependency): Option[FronttierException] = {
    if (database.isInstalled(dependency)) {
      for (file <- database.files(dependency)) {
        logger.info("deleting " + (destination / file))
        (destination / file).delete(recursive = true)
      }
      logger.info("Removing from database")
      database.delete(dependency)
      None
    } else {
      logger.error(s"$dependency not installed at the destination location")
      Some(UninstalledDependencyException(dependency))
    }
  }

  def deleteAll(): Option[FronttierException] = {
    if (database.installed.nonEmpty) {
      for (each <- database.installed) {
        for (file <- database.files(each)) {
          logger.info("deleting " + (destination / file))
          (destination / file).delete(recursive = true)
        }
      }
      logger.info("Cleaning database")
      database.clear()
      None
    } else {
      logger.error("There are no dependencies to remove")
      Some(EmptyLocationException(destination.pathString))
    }
  }

  def isInstalled(dependency : Dependency) = database.isInstalled(dependency)

  def isInCache(dependency : Dependency) = cache.exists(dependency)

  lazy val cache = configuration.cache

  def parserFileAt(location : FileUrl) = parsers.find {_.existsFileAt(location)}

  def configuration(location : FileUrl) : Option[Configuration] = {
    for (parser <- parserFileAt(location)) yield parser.parseAt(workingDir)
  }
}

object Fronttier {

  val defaultWorkingDir = Paths.DefaultWorkingDir

  val parsers: Seq[ConfigParser] = Seq(FronttierConfigParser, XmlConfigParser, XmlAttrConfigParser)

  def apply(workingDir : FileUrl, destination: FileUrl, config : FronttierConfiguration) : Fronttier = {
    new Fronttier(workingDir, destination, parsers, config)
  }

  def apply(destination : FileUrl, config : FronttierConfiguration) : Fronttier = {
    apply(defaultWorkingDir, destination, config)
  }

  def apply(config : FronttierConfiguration) : Fronttier = {
    apply(config.destination, config)
  }

  def apply(destination: FileUrl) : Fronttier = apply(destination, FronttierDefaults.defaults.toConfig)

}

