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

class Repository(val kind : String, val url : String) {
	private val knownFiles = Seq(".js", ".css", ".svg", ".png", ".jpg", ".pdf", "..ttf", ".otf", ".woff", ".eot")

	private val validKinds =
		Set(Repository.HttpKind, Repository.GitKind, Repository.SvnKind)
	require(!kind.isEmpty && validKinds.contains(kind))
	require(!url.isEmpty)
	override def toString = s"$kind :: $url"
	override def equals(other : Any) = other match {
		case rep : Repository => this.kind == rep.kind && this.url == rep.url
		case _ => false
	}
	override def hashCode() = 14107 * kind.hashCode + url.hashCode
	def copy = new Repository(kind, url)

	def isGit = kind == Repository.GitKind
	def isSvn = kind == Repository.SvnKind
	def isHttp = kind == Repository.HttpKind

	def download(destination : String, dependency : Dependency) : Boolean = {
		kind match {
			case Repository.HttpKind => HttpDownloader.download(destination, url, dependency)
			case Repository.GitKind => GitDownloader.download(destination, url, dependency)
			case Repository.SvnKind => SvnDownloader.download(destination, url, dependency)
			case _ => false
		}
	}

	def download(destination : String) : Boolean = {
		if (isDownloadable()) HttpDownloader.download(destination, url)
		else false
	}

	def isDownloadable() = knownFiles.exists(e => url.endsWith(e))
}
object Repository {
	val HttpKind = "http"
	val GitKind = "git"
	val SvnKind = "svn"
	def apply(kind : String, url : String) = {
		new Repository(if (!kind.isEmpty) kind else Repository.HttpKind, url)
	}
	def apply(kind : Option[String], url : String) = {
		new Repository(kind.getOrElse(Repository.HttpKind), url)
	}
	def unnaply(rep : Repository) = (rep.kind, rep.url)
}