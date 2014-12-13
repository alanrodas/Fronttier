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

import com.alanrodas.fronttier.io._
import com.alanrodas.fronttier.parsing._
import com.typesafe.scalalogging.slf4j.LazyLogging
import rapture.fs._
import platform.adaptive
import rapture.net._
import rapture.uri._

class Dependency(val group: String, val name: String, val version: String, val maybeUri : Option[String])
    extends LazyLogging {

  require(!group.isEmpty, "The group of a dependency cannot be empty")
  require(!name.isEmpty, "The name of a dependency cannot be empty")
  require(!version.isEmpty, "The version of a dependency cannot be empty")

  override def toString = s"$group :: $name :: $version"

  override def equals(other: Any) = other match {
    case dep: Dependency => this.group == dep.group &&
        this.name == dep.name && this.version == dep.version
    case _ => false
  }

  override def hashCode() = 20477 * group.hashCode + name.hashCode + version.hashCode

  def copy = new Dependency(group, name, version, maybeUri)

  def path : SimplePath = new SimplePath(group.split("\\.") ++ Seq(name, version), Map())

  def download(destination: FileUrl, repositories: Seq[Repository])
      (implicit fronttier : Fronttier):(Option[Configuration], Seq[FronttierException]) = {
    val maybeConf = (for (repo <- repositories) yield repo.download(destination, this)).find(_.isDefined).flatten
    if (maybeConf.isEmpty) (None, Seq(UnnavailableDependencyException(this, repositories)))
    else (Some(maybeConf.get._1), maybeConf.get._2)

  }
}

object Dependency {

  def apply(group: String, name: String, version: String, uri : String) = {
    new Dependency(group, name, version, Some(uri))
  }

  def apply(group: String, name: String, version: String) = {
    new Dependency(group, name, version, None)
  }

  def unnaply(dep: Dependency) = (dep.group, dep.name, dep.version, dep.maybeUri)
}