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

import rapture.core.strategy.throwExceptions
import rapture.fs._
import rapture.fs.platform.adaptive
import rapture.core.strategy.throwExceptions

trait Cache {

  def exists(dependency: Dependency) : Boolean
  def load(dependency: Dependency, destination : FileUrl)(implicit fronttier : Fronttier) : Unit
  def save(dependency: Configuration, destination : FileUrl)(implicit fronttier : Fronttier) : Unit
  def delete(dependency: Dependency) : Unit
  def location(dependency: Dependency) : Option[FileUrl]
  def clear() : Unit
}

object Cache {
  def apply() : LocationCache = apply(Paths.CacheDir)
  def apply(path : FileUrl) : LocationCache = LocationCache(path)
  def unapply(cache : Cache) : Option[FileUrl] =
    if (cache.isInstanceOf[LocationCache]) Some(cache.asInstanceOf[LocationCache].cacheDir) else None
}

case class LocationCache(val cacheDir : FileUrl) extends Cache {

  def exists(dependency: Dependency) = location(dependency).isDefined
  def load(dependency: Dependency, destination : FileUrl)(implicit fronttier : Fronttier) = {
    println(Console.MAGENTA + "LOADING FROM CACHE ")
    val dependencyPath = cacheDir / dependency.path
    println(Console.MAGENTA + "DEPENDENCY PATH " + dependencyPath)
    // val configuration = fronttier.configuration()
    //for (location <- (dependency)) location.copyTo(dest / dependency.path)
  }
  def save(dependency: Configuration, destination : FileUrl)(implicit fronttier : Fronttier) = {
    val dependencyPath = dependency.currentDependency.path.pathString
    val cacheDepFolder = cacheDir /
        (if (dependencyPath.startsWith(implicitly[Platform].separator)) dependencyPath.drop(1) else dependencyPath)

    val parser = fronttier.parserFileAt(destination)
    if (parser.isDefined) {
      if (!cacheDepFolder.exists) cacheDepFolder.mkdir(makeParents = true)
      // Copy all files
      for (file <- dependency.files) {
        val cachedFile = (cacheDepFolder / file)
        val folderName = "file://" + cachedFile
            .pathString
            .substring(0, cachedFile.pathString.lastIndexOf(implicitly[Platform].separator))
        val folder = File.parse(folderName)
        if (!folder.exists) folder.mkdir(makeParents = true)
        (destination / file).copyTo(cacheDepFolder / file, overwrite = true, recursive = true)
      }
      // Copy the declaration file
      (destination / parser.get.fileName).copyTo(cacheDepFolder / parser.get.fileName, overwrite = true, recursive = true)
    }
    }

  def delete(dependency: Dependency) = for (uri <- location(dependency) ) uri.delete(recursive = true)
  def location(dependency: Dependency): Option[FileUrl] =
    if ((cacheDir / dependency.path).exists) Some(cacheDir / dependency.path) else None
  def clear() = cacheDir.delete(recursive = true)
}

object NoCache extends Cache {
  def exists(dependency: Dependency) = false
  def load(dependency: Dependency, destination : FileUrl)(implicit fronttier : Fronttier) = {}
  def save(dependency: Configuration, destination : FileUrl)(implicit fronttier : Fronttier) = {}
  def delete(dependency: Dependency) = {}
  def location(dependency: Dependency): Option[FileUrl] = None
  def clear() = {}
}