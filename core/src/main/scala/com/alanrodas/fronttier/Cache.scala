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

import rapture.core.strategy.throwExceptions
import rapture.fs._
import rapture.fs.platform.adaptive
import rapture.core.strategy.throwExceptions

trait Cache {

  def exists(dependency: Dependency)(implicit fronttier : Fronttier) : Boolean
  def load(dependency: Dependency, destination : FileUrl)(implicit fronttier : Fronttier) : Unit
  def save(dependency: Configuration, destination : FileUrl)(implicit fronttier : Fronttier) : Unit
  def delete(dependency: Dependency)(implicit fronttier : Fronttier) : Unit
  def clear()(implicit fronttier : Fronttier) : Unit
}

object Cache {
  def apply() : LocationCache = apply(Paths.CacheDir)
  def apply(path : FileUrl) : LocationCache = LocationCache(path)
  def unapply(cache : Cache) : Option[FileUrl] =
    if (cache.isInstanceOf[LocationCache]) Some(cache.asInstanceOf[LocationCache].cacheDir) else None
}

case class LocationCache(val cacheDir : FileUrl) extends Cache {

  def dependencyUri(dependency: Dependency) = {
    val dependencyPath = dependency.path.pathString
    val cacheDepFolder = cacheDir /
        (if (dependencyPath.startsWith(implicitly[Platform].separator)) dependencyPath.drop(1) else dependencyPath)
    if (cacheDepFolder.exists) Some(cacheDepFolder) else None
  }

  def exists(dependency: Dependency)(implicit fronttier : Fronttier) = {
    dependencyUri(dependency).map(folder => fronttier.parserFileAt(folder)).isDefined
  }
  def load(dependency: Dependency, destination : FileUrl)(implicit fronttier : Fronttier) = {
    val cacheLoc = dependencyUri(dependency)
    if (cacheLoc.isDefined) {
      for (configParser <- fronttier.parserFileAt(cacheLoc.get)) {
        val configuration = configParser.parseAt(cacheLoc.get)
        //location.copyTo(dest / dependency.path)
        for (file <- configuration.files) {
          val destFile = destination / file
          val folderName = "file://" + destFile
              .pathString
              .substring(0, destFile.pathString.lastIndexOf(implicitly[Platform].separator))
          val folder = File.parse(folderName)
          if (!folder.exists) folder.mkdir(makeParents = true)
          (cacheLoc.get / file).copyTo(destination / file)
          fronttier.database.save(configuration)
        }
        for(dependency <- configuration.dependencies) {
          fronttier.download(dependency)
        }
      }
    }
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
        val cachedFile = cacheDepFolder / file


        val folderName = "file://" + cachedFile
            .pathString
            .substring(0, cachedFile.pathString.lastIndexOf(implicitly[Platform].separator))
        val folder = File.parse(folderName)
        if (!folder.exists) folder.mkdir(makeParents = true)
        (destination / file).copyTo(cacheDepFolder / file, overwrite = true, recursive = true)
      }
      // Copy the declaration file
      (destination / parser.get.fileName).copyTo(cacheDepFolder / parser.get.fileName, overwrite = true, recursive = true)
      fronttier.database.save(dependency)
    }
  }

  def delete(dependency: Dependency)(implicit fronttier : Fronttier) = {
    for (folder <- dependencyUri(dependency)) {
      deleteDirectory(folder.javaFile)
    }
  }
  def clear()(implicit fronttier : Fronttier) = {
    for (child : FileUrl <- cacheDir.children) {
      val result = deleteDirectory(child.javaFile)
    }
  }

  def deleteDirectory(directory : java.io.File) : Boolean = {
    if(directory.exists()){
      val files = directory.listFiles()
      if(null!=files){
        for(i <- 0 until files.length) {
          if(files(i).isDirectory()) {deleteDirectory(files(i))}
          else {files(i).delete()}
        }
      }
    }
    directory.delete()
  }
}

object NoCache extends Cache {
  def exists(dependency: Dependency)(implicit fronttier : Fronttier) = false
  def load(dependency: Dependency, destination : FileUrl)(implicit fronttier : Fronttier) = {}
  def save(dependency: Configuration, destination : FileUrl)(implicit fronttier : Fronttier) = {}
  def delete(dependency: Dependency)(implicit fronttier : Fronttier) = {}
  def clear()(implicit fronttier : Fronttier) = {}
}