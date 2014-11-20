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

import com.alanrodas.fronttier.io.Terminal
import rapture.fs.{File, FileUrl}
import rapture.fs.platform.adaptive
import com.alanrodas.fronttier.io._
import rapture.core.strategy.throwExceptions

case class UnnavailableDependencyException(dep : Dependency, repos : Seq[Repository])
	extends RuntimeException("The following dependency was not found: " + dep +
			"\nIn any of the following repositories:\n" + repos.mkString("\n"))
case class UnnavailableRepositoryException(repo : Repository)
		extends RuntimeException("The following downloadable repository was not found: " + repo)

case class Configuration(repositories : Seq[Repository], dependencies : Seq[Dependency]) {

	def download(cache : Cache, destination : String)(implicit terminal : Terminal = Terminal(false)) = {
		val destinationFile = destination.asFile
		if (!destinationFile.exists) destinationFile.mkdir(true)
		for (dep <- dependencies) {
			// If they are already in the cache, use that one
			if (!dep.installedIn(destination) || cache.force) {
				terminal.info("Fetching " + dep)
				downloadOne(dep, cache, destination)
			} else {
				terminal.info(dep + " already installed at " + destination.asFile.pathString)
			}
		}
		// download direct repository dependencies
		repositories.filter(_.isDownloadable()).foreach {each =>
			terminal.info("Fetching " + each)
			terminal.info("Donwloading...")
			val downloaded = each.download(destination)
			if (!downloaded) throw UnnavailableRepositoryException(each)
			else {terminal.info("Donwloaded")}
		}
	}

	private def downloadOne(dependency : Dependency, cache : Cache, destination : String)
			(implicit terminal : Terminal = Terminal(false)) = {
		if (cache.exists(dependency)) {
			terminal.info(cache.existsLocally(dependency),
				"Copying from Local Cache", "Copying from Global Cache")
			cache.copy(dependency).to(destination)
		} else {
			terminal.info("Donwloading...")
			dependency.download(destination, repositories)
			terminal.info("Donwloaded " + dependency)
			terminal.info("Copying to cache")
			cache.copy(dependency).from(destination)
			terminal.info("Copied")
		}
	}
}