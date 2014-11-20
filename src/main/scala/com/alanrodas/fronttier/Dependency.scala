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

import rapture.fs.{File, FileUrl}
import com.alanrodas.fronttier.parsing._
import com.alanrodas.fronttier.io._
import rapture.fs.platform.adaptive

object FileNameParser extends ExtendedRegexParsers {
	override type ResultType = List[String]
	private def separator = whiteSpace.? ~> "::" <~ whiteSpace.?

	private def group : Parser[String] = "[a-zA-Z0-9]([a-zA-Z0-9-_.]*[a-zA-Z0-9])?".r
	private def name : Parser[String] = "[a-zA-Z0-9][a-zA-Z0-9-_]*".r
	private def version : Parser[String] = "[a-zA-Z0-9][a-zA-Z0-9-.]*".r

	private def dependency = group ~ separator ~ name ~ (separator ~> version).? ^^ { case group ~ _ ~ name ~ version =>
		Dependency(group, name, version)
	}
	private def dependencyFiles = "[" ~ dependency ~ "]" ~ whiteSpace.+ ~ listFileNames ^^ {
		case _~dep~_~_~listNames => dep -> listNames
	}
	def dependenciesFiles = discardWhites( (dependencyFiles <~ whiteSpace.*).* ) ^^ {_.toMap}
	def listFileNames = discardWhites( (fileName <~ whiteSpace.*).* )
}

class Dependency(val group : String, val name : String, val version : String) {

	import rapture.net.HttpUrl
	import rapture.uri.Url

	require(!group.isEmpty)
	require(!name.isEmpty)
	require(!version.isEmpty)
	private var _files : List[String] = null

	override def toString = s"$group :: $name :: $version"
	override def equals(other : Any) = other match {
		case dep : Dependency => this.group == dep.group &&
				this.name == dep.name && this.version == dep.version
		case _ => false
	}
	override def hashCode() = 20477 * group.hashCode + name.hashCode + version.hashCode
	def copy = new Dependency(group, name, version)

	def location(implicit uri : FileUrl = File.currentDir) = {
		group.split("\\.").foldLeft(uri) {(a,e) => a / e} / name / version
	}

	def remoteLocation(implicit uri : HttpUrl) = {
		group.split("\\.").foldLeft(uri) {(a,e) => a / e} / name / version
	}

	def declarationFile(implicit folder : FileUrl = File.currentDir) = {
		folder / Dependency.DeclarationsFile
	}

	def remoteDeclarationFile(implicit folder : HttpUrl) = {
		folder / Dependency.DeclarationsFile
	}

	def files(implicit fileUri : FileUrl = File.currentDir) = {
		if (_files == null) {

			val parsed = FileNameParser.parseAll(
				FileNameParser.listFileNames, declarationFile.contents)
			if (parsed.successful) _files = parsed.get
			else {
				throw new FileNameDeclarationsException("There were invalid filenames in " + declarationFile.pathString)
			}
		}
		_files
	}

	def installedIn(destination : String) : Boolean = {
		// TODO Fronttier will not check if dependencies are installed. Change in the future
		false
	}

	def download(destination : String, repositories : Seq[Repository]): Unit = {
		var downloaded = false
		var current = 0
		while(!downloaded && current < repositories.length) {
			val repo = repositories(current)
			downloaded = repo.download(destination, this)
			current += 1
		}
		if (!downloaded) throw UnnavailableDependencyException(this, repositories)
	}
}

object Dependency {
	val DefaultVersion = "LATEST"
	val DeclarationsFile = "dependencies.ftd"

	def apply(group : String, name : String, version : String) = {
		new Dependency(group, name, if (!version.isEmpty) version else Dependency.DefaultVersion)
	}
	def apply(group : String, name : String, version : Option[String]) = {
		new Dependency(group, name, version.getOrElse(Dependency.DefaultVersion))
	}
	def unnaply(dep : Dependency) = (dep.group, dep.name, dep.version)
}