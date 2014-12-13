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

import rapture.fs.FileUrl

class Repository(val kind: RepositoryKind, val url: String) {
  private val knownFiles = Seq(".js", ".css", ".svg", ".png", ".jpg", ".pdf", "..ttf", ".otf", ".woff", ".eot")

  require(!url.isEmpty, "The url of a repository cannot be empty")

  override def toString = s"$kind :: $url"

  override def equals(other: Any) = other match {
    case rep: Repository => this.kind == rep.kind && this.url == rep.url
    case _ => false
  }

  override def hashCode() = 14107 * kind.hashCode + url.hashCode

  def copy = new Repository(kind, url)

  def isGit = kind == GitKind
  def isSvn = kind == SvnKind
  def isHttp = kind == HttpKind

  def download(destination: FileUrl, dependency: Dependency)
      (implicit fronttier : Fronttier): Option[(Configuration, Seq[FronttierException])] = {
    kind match {
      case HttpKind => HttpDownloader.download(destination, url, dependency)
      case GitKind => GitDownloader.download(destination, url, dependency)
      case SvnKind => SvnDownloader.download(destination, url, dependency)
      case _ => None
    }
  }

  def isDownloadable() = knownFiles.exists(e => url.endsWith(e))
}

object Repository {

  val defaults = Seq(
    apply(HttpKind, "http://alanrodas.com/repository")
  )

  def apply(kind: RepositoryKind, url: String) : Repository  = {
    new Repository(kind, url)
  }

  def apply(kind: String, url: String) : Repository = {
    apply(RepositoryKind(kind), url)
  }

  def apply(kind: Option[String], url: String) : Repository = {
    apply(kind.getOrElse("http"), url)
  }

  def unapply(rep: Repository) = (rep.kind, rep.url)
}

sealed trait RepositoryKind {
  override def toString = this match {
    case RepositoryKind(name) => name
    case _ => "Other"
  }
}
object RepositoryKind {
  def apply(str : String) : RepositoryKind = {
    str match {
      case "http" => HttpKind
      case "git" => GitKind
      case "svn" => SvnKind
      case _ => HttpKind
    }
  }
  def unapply(repoKind : RepositoryKind) : Option[String] = {
    repoKind match {
      case HttpKind => Some("http")
      case GitKind => Some("git")
      case SvnKind => Some("svn")
    }
  }
}

case object HttpKind extends RepositoryKind
case object GitKind extends RepositoryKind
case object SvnKind extends RepositoryKind