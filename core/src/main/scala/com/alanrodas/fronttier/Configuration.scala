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

import com.alanrodas.fronttier.io._
import com.alanrodas.scaland.logging._
import rapture.core.strategy.throwExceptions
import rapture.fs.FileUrl

class Configuration(val currentDependency : Dependency,
    val repositories: Seq[Repository], val dependencies: Seq[Dependency], val files : Seq[String])
    extends LazyLogging {

  val group = currentDependency.group
  val name = currentDependency.name
  val version = currentDependency.version

  def download(destination: FileUrl)
      (implicit fronttier : Fronttier) : (Configuration, Seq[FronttierException]) = {
    // Create destination if it doesn't exist
    fronttier.configureLogger(logger)
    if (!destination.exists) {
      logger.info("Creating destination folder")
      destination.mkdir(makeParents = true)
    }

    (this, if (!fronttier.isInstalled(currentDependency)) {
      // Download dependencies
      dependencies.map {dependency =>
        logger.info("Found " + dependency + " as a dependency of " + currentDependency)
        if (fronttier.isInCache(dependency)) {
          // Copy from cache
          logger.info(s"The dependency $dependency exists in cache, copying from the cache to destination")
          fronttier.cache.load(dependency, destination)
          Nil
        } else {
          // Download
          val (config, errors) = dependency.download(destination, repositories)
          errors
        }
      }.flatten
    } else {
      logger.info(s"The dependency $currentDependency is already installed")
      Nil
    })

  }

  override def toString() = currentDependency.toString
}

object Configuration {
  def apply(group : String, name : String, version : String,
            repositories: Seq[Repository], dependencies: Seq[Dependency], files : Seq[String]) = {
    new Configuration(Dependency(group, name, version), Repository.defaults ++ repositories, dependencies, files)
  }

  def apply(currentDependency : Dependency, repositories: Seq[Repository], dependencies: Seq[Dependency], files : Seq[String]) = {
    new Configuration(currentDependency, Repository.defaults ++ repositories, dependencies, files)
  }

  def unnaply(conf : Configuration) = (conf.group, conf.name, conf.version,
      conf.repositories, conf.dependencies, conf.files)
}