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

import rapture.uri._
import rapture.net._

import rapture.io._
import rapture.fs._
import rapture.fs.platform.adaptive
import rapture.core.strategy.throwExceptions
import com.alanrodas.fronttier.io._

trait Cache {

	protected trait DependencyCache {
		def dependency : Dependency
		def force : Boolean
		def cache : Cache
		def to(dest : String) : Unit
		def from(dest : String) : Unit
	}
	def force : Boolean
	def exists(dependency : Dependency) = existsLocally(dependency) || existsGlobally(dependency)
	def existsLocally(dependency : Dependency) = !force && locationAt(dependency, Cache.LocalCacheDir).isDefined
	def existsGlobally(dependency : Dependency) = !force && locationAt(dependency, Cache.GlobalCacheDir).isDefined
	def copy(dependency : Dependency) : DependencyCache
	def delete(dependency : Dependency) : Unit
	def location(dependency : Dependency) : Option[FileUrl] = {
		val local = locationAt(dependency, Cache.LocalCacheDir)
		if (local.isDefined) local else locationAt(dependency, Cache.GlobalCacheDir)
	}
	private def locationAt(dependency : Dependency, fileUrl : FileUrl) = {
		implicit val location = dependency.location(fileUrl)
		if (dependency.declarationFile.exists) Some(location)
		else None
	}
}

case class FolderBasedCache(uri : FileUrl, force : Boolean) extends Cache {

	protected class FolderBasedDependencyCache(val dependency : Dependency,
			val force : Boolean, val cache : Cache) extends DependencyCache {
		def to(dest : String) {
			cache.location(dependency).fold(){implicit location =>
				for (file <- dependency.files) {
					(location / file).copyTo(dest.asFile / file, overwrite = true)
				}
			}
		}
		def from(dest : String) = {
			val cacheUri = cache.asInstanceOf[FolderBasedCache].uri
			val fromUrl = dest.asFile
			val saveTo = dependency.location(cacheUri)
			if (saveTo.exists) {
				saveTo.delete(recursive = true)
			}
			saveTo.mkdir(makeParents = true)
			for (file <- dependency.files(fromUrl) ) {
				(fromUrl / file).copyTo(saveTo / file)
			}
		}
	}

	def copy(dependency : Dependency) = {
		if (force && exists(dependency)) delete(dependency)
		new FolderBasedDependencyCache(dependency, force, this)
	}
	def delete(dependency : Dependency) = {
		location(dependency).fold(){location => location.delete() }
	}
}

case class NoCache(force : Boolean) extends Cache {
	protected class NoCacheDependencyCache(val dependency : Dependency,
			val force : Boolean, val cache : Cache) extends DependencyCache {
		def to(dest : String) {}
		def from(dest : String) {}
	}
	def copy(dependency : Dependency) = new NoCacheDependencyCache(dependency, force, this)
	def delete(dependency : Dependency) = {}
}

object Cache {
	val GlobalCacheDir = Fronttier.DefaultGlobalDir / "cache"
	val GlobalPluginsCacheDir = Fronttier.DefaultGlobalDir / "plugins"
	val LocalCacheDir = Fronttier.DefaultLocalDir / "cache"
	val LocalPluginsCacheDir = Fronttier.DefaultLocalDir / "plugins"

	def Global(force : Boolean = false) = new FolderBasedCache(GlobalCacheDir, force)
	def Local(force : Boolean = false) = new FolderBasedCache(LocalCacheDir, force)
}