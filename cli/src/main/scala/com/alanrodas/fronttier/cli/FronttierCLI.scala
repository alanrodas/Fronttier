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

package com.alanrodas.fronttier.cli

import com.alanrodas.fronttier._
import com.alanrodas.fronttier.io._
import com.alanrodas.fronttier.parsing._

import com.alanrodas.scaland.cli._
import com.alanrodas.scaland.cli.runtime._
import com.alanrodas.scaland.logging._

import language.postfixOps

object FronttierCLI extends CLIApp with LazyLogging {

  private def getConfiguration(commandCall : Command) = {
    try {
      require(!(commandCall argument "config" isDefined),
        "Selection of configuration parser has been removed as of Fronttier 0.2. " +
            "The correct configuration parser is now automatically detected.")
      require(!(commandCall argument "filename" isDefined),
        "Selection of file name has been removed as of Fronttier 0.2 in favor of a convention " +
            "fronttier.ftt or fronttier.xml file, depending on the configuration parser used")
      require(!(commandCall argument "load" isDefined),
        "Loading plugins from the command line has been removed as of Fronttier 0.2. " +
            "See the section about \"creating plugins\" at http://fronttier.alanrodas.com/docs/")
      require(!(commandCall flag "nocache" isDefined),
        "\"nocache\" has been renamed to \"no-cache\" as of Fronttier 0.2.")
      require(!(commandCall flag "local" isDefined),
        "Local plugins and cache has been removed as of Fronttier 0.2 in favor of " +
            "global user cache or no cache")
      require(!(commandCall flag "global" isDefined),
        "Setting global cache has been removed as of Fronttier 0.2. " +
            "Use \"use-cache\" instead.")
    } catch {
      case e:IllegalArgumentException =>
        // Show an errorreporting that this has been removed. In further versions
        // this warnings will be removed, and an unknown argument is thrown
        logger.error(e.getMessage.replace("requirement failed", "[IGNORED]"))
    }

    val useCache = {
      clRequire(!((commandCall flag "use-cache") && (commandCall flag "no-cache")),
        "Only one of \"no-cache\" or \"use-cache\" can be defined at the same time.")
      if (commandCall flag "no-cache") Some(false)
      else Some(true)
    }
    val force = {
      if (commandCall flag "force") Some(true) else None
    }
    val verbose = {
      if (commandCall flag "verbose") Some(true) else None
    }
    val destination = {
      if (commandCall argument "destination")
        Some((commandCall argument "destination").valueAs[String](0))
      else None
    }

    (FronttierDefaults.defaults overritenBy FronttierDefaults(
        useCache = useCache,
        verbose = verbose,
        force = force,
        destination = destination)
    ).toConfig.configureLogger(logger)
  }

  private def errorHandledExecution(config : FronttierConfiguration)(f : (FronttierConfiguration) => Unit) = {
    try {
      logger.level = Info
      logger.info("Starting fronttier...")
      config.configureLogger(logger)
      f(config)
    } catch {
      case e: FileNameDeclarationsException =>
        logger.error(e.getMessage)
      case e: IllegalCommandLineArgumentsException =>
        logger.error(e.getMessage)
      case e: Exception =>
        if (e.getMessage != null)
          logger.error(e.getMessage)
        else {
          logger.error("An unknown error has ocurred. Please, send the following to the developers.")
          e.printStackTrace()
        }
    }
  }

  /** Download all dependencies from the configuration file at current location. */
  root accepts(
      // Ignored & deprecated
      flag named "global" alias "g",
      flag named "local" alias "l",
      flag named "nocache" alias "n",
      arg named "filename" alias "F" taking 1 as "fronttier.ftt",
      arg named "config" alias "c" taking 1 as "fronttier",
      arg named "load" alias "L" taking 10 values,
      // Still usable
      flag named "force" alias "f",
      flag named "verbose" alias "v",
      arg named "destination" alias "d" taking 1 as ".",
      // New and renamed
      flag named "no-cache",
      flag named "use-cache"
  ) does { cmd =>
    errorHandledExecution(getConfiguration(cmd)){ config =>
      Fronttier(config).downloadRoot()
    }
  }

  /** Install a dependency to the current project */
  command named "install" accepts(
      // Ignored & deprecated
      flag named "global" alias "g",
      flag named "local" alias "l",
      flag named "nocache" alias "n",
      arg named "filename" alias "F" taking 1 as "fronttier.ftt",
      arg named "config" alias "c" taking 1 as "fronttier",
      arg named "load" alias "L" taking 10 values,
      // Still usable
      flag named "force" alias "f",
      flag named "verbose" alias "v",
      arg named "destination" alias "d" taking 1 as ".",
      // New and renamed
      flag named "no-cache",
      flag named "use-cache"
  ) receives (
      value named "group" mandatory,
      value named "name" mandatory,
      value named "version" mandatory
  ) does { cmd =>
    errorHandledExecution(getConfiguration(cmd)){ config =>
      val (group, name, version) =
        ((cmd value "group").valueAs[String], (cmd value "name").valueAs[String], (cmd value "version").valueAs[String])
      clRequire(group.nonEmpty, "The group of the dependency cannot be empty")
      clRequire(name.nonEmpty, "The name of the dependency cannot be empty")
      clRequire(version.nonEmpty, "The version of the dependency cannot be empty")

      val dependency = Dependency(group, name, version)
      logger.info(s"Installing dependency: $dependency")
      val result = Fronttier(config).download(dependency)
      if (result.nonEmpty) {
        logger.level = Info
        logger.info("Fronttier finalized successfuly")
      }
    }
  }

  /** Install a dependency to the current project */
  command named "uninstall" accepts(
      // Ignored & deprecated
      flag named "global" alias "g",
      flag named "local" alias "l",
      flag named "nocache" alias "n",
      arg named "filename" alias "F" taking 1 as "fronttier.ftt",
      arg named "config" alias "c" taking 1 as "fronttier",
      arg named "load" alias "L" taking 10 values,
      // Still usable
      flag named "force" alias "f",
      flag named "verbose" alias "v",
      arg named "destination" alias "d" taking 1 as ".",
      // New and renamed
      flag named "no-cache",
      flag named "use-cache"
      ) receives (
      value named "group" mandatory,
      value named "name" mandatory,
      value named "version" mandatory
      ) does { cmd =>
    errorHandledExecution(getConfiguration(cmd)){ config =>
      val (group, name, version) =
        ((cmd value "group").valueAs[String], (cmd value "name").valueAs[String], (cmd value "version").valueAs[String])
      clRequire(group.nonEmpty, "The group of the dependency cannot be empty")
      clRequire(name.nonEmpty, "The name of the dependency cannot be empty")
      clRequire(version.nonEmpty, "The version of the dependency cannot be empty")

      val dependency = Dependency(group, name, version)
      logger.info(s"Uninstalling dependency: $dependency")
      val result = Fronttier(config).delete(dependency)
      if (result.isEmpty) {
        logger.level = Info
        logger.info("Fronttier finalized successfuly")
      }
    }
  }

  /** Delete all dependencies from current location */
  command named "clear" accepts(
      // Ignored & deprecated
      flag named "global" alias "g",
      flag named "local" alias "l",
      flag named "nocache" alias "n",
      arg named "filename" alias "F" taking 1 as "fronttier.ftt",
      arg named "config" alias "c" taking 1 as "fronttier",
      arg named "load" alias "L" taking 10 values,
      // Still usable
      flag named "force" alias "f",
      flag named "verbose" alias "v",
      arg named "destination" alias "d" taking 1 as ".",
      // New and renamed
      flag named "no-cache",
      flag named "use-cache"
      ) does { cmd =>
    errorHandledExecution(getConfiguration(cmd)){ config =>
      logger.info(s"Deleting all dependencies")
      val result = Fronttier(config).deleteAll()
      if (result.isEmpty) {
        logger.level = Info
        logger.info("Fronttier finalized successfuly")
      }
    }
  }

  /** Save a dependency to cache */
  command named "cache" accepts(
      // Ignored & deprecated
      flag named "global" alias "g",
      flag named "local" alias "l",
      flag named "nocache" alias "n",
      arg named "filename" alias "F" taking 1 as "fronttier.ftt",
      arg named "config" alias "c" taking 1 as "fronttier",
      arg named "load" alias "L" taking 10 values,
      // Still usable
      flag named "force" alias "f",
      flag named "verbose" alias "v",
      arg named "destination" alias "d" taking 1 as ".",
      // New and renamed
      flag named "no-cache",
      flag named "use-cache"
  ) receives (
      value named "group" mandatory,
      value named "name" mandatory,
      value named "version" mandatory
  ) does { cmd =>
    errorHandledExecution(getConfiguration(cmd)){ config =>
      val (group, name, version) =
        ((cmd value "group").valueAs[String], (cmd value "name").valueAs[String], (cmd value "version").valueAs[String])
      clRequire(group.nonEmpty, "The group of the dependency cannot be empty")
      clRequire(name.nonEmpty, "The name of the dependency cannot be empty")
      clRequire(version.nonEmpty, "The version of the dependency cannot be empty")

      val dependency = Dependency(group, name, version)
      implicit val fronttier = Fronttier(config.copy(useCache = true))
      val isInstalled = fronttier.isInstalled(dependency)
      fronttier.download(dependency)
      if (!isInstalled) fronttier.delete(dependency)
      fronttier.database.deleteThis()
    }
  }

  /** Remove a dependency from cache */
  command named "uncache" accepts(
      // Ignored & deprecated
      flag named "global" alias "g",
      flag named "local" alias "l",
      flag named "nocache" alias "n",
      arg named "filename" alias "F" taking 1 as "fronttier.ftt",
      arg named "config" alias "c" taking 1 as "fronttier",
      arg named "load" alias "L" taking 10 values,
      // Still usable
      flag named "force" alias "f",
      flag named "verbose" alias "v",
      arg named "destination" alias "d" taking 1 as ".",
      // New and renamed
      flag named "no-cache",
      flag named "use-cache"
  ) receives (
      value named "group" mandatory,
      value named "name" mandatory,
      value named "version" mandatory
  ) does { cmd =>
    errorHandledExecution(getConfiguration(cmd)){ config =>
      val (group, name, version) =
        ((cmd value "group").valueAs[String], (cmd value "name").valueAs[String], (cmd value "version").valueAs[String])
      clRequire(group.nonEmpty, "The group of the dependency cannot be empty")
      clRequire(name.nonEmpty, "The name of the dependency cannot be empty")
      clRequire(version.nonEmpty, "The version of the dependency cannot be empty")

      implicit val fronttier = Fronttier(config.copy(useCache = true))
      Cache().delete(Dependency(group, name, version))
      fronttier.database.deleteThis()
    }
  }

  /** Clear the cache */
  command named "clean-cache" accepts(
      // Ignored & deprecated
      flag named "global" alias "g",
      flag named "local" alias "l",
      flag named "nocache" alias "n",
      arg named "filename" alias "F" taking 1 as "fronttier.ftt",
      arg named "config" alias "c" taking 1 as "fronttier",
      arg named "load" alias "L" taking 10 values,
      // Still usable
      flag named "force" alias "f",
      flag named "verbose" alias "v",
      arg named "destination" alias "d" taking 1 as ".",
          // New and renamed
      flag named "no-cache",
      flag named "use-cache"
  ) does { cmd =>
    errorHandledExecution(getConfiguration(cmd)){ config =>

      implicit val fronttier = Fronttier(config.copy(useCache = true))
      Cache().clear()
      fronttier.database.deleteThis()
    }
  }
  ////////////////////////////////////////////////////
  // Removed commands

  command named "plug" accepts(
      // Ignored & deprecated
      flag named "global" alias "g",
      flag named "local" alias "l",
      flag named "nocache" alias "n",
      arg named "filename" alias "F" taking 1 as "fronttier.ftt",
      arg named "config" alias "c" taking 1 as "fronttier",
      arg named "load" alias "L" taking 10 values,
      // Still usable
      flag named "force" alias "f",
      flag named "verbose" alias "v",
      arg named "destination" alias "d" taking 1 as ".",
      // New and renamed
      flag named "no-cache",
      flag named "use-cache"
  ) minimumOf 1 does { cmd =>
    logger.error("The plug command has been removed as of Fronttier 0.2. " +
        "Plugins are now removed, favoring the SBT plugin extensions and alike.")
  }

  command named "unplug" accepts(
      // Ignored & deprecated
      flag named "global" alias "g",
      flag named "local" alias "l",
      flag named "nocache" alias "n",
      arg named "filename" alias "F" taking 1 as "fronttier.ftt",
      arg named "config" alias "c" taking 1 as "fronttier",
      arg named "load" alias "L" taking 10 values,
      // Still usable
      flag named "force" alias "f",
      flag named "verbose" alias "v",
      arg named "destination" alias "d" taking 1 as ".",
      // New and renamed
      flag named "no-cache",
      flag named "use-cache"
  ) minimumOf 1 does { cmd =>
    logger.error("The unplug command has been removed as of Fronttier 0.2. " +
        "Plugins are now removed, favoring the SBT plugin extensions and alike.")

  }
  ////////////////////////////////////////////////////

}
