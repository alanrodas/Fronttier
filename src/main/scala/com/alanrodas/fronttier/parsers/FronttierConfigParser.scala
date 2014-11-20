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
package com.alanrodas.fronttier.parsers

// import scalax.file.Path
import com.alanrodas.fronttier._
import com.alanrodas.fronttier.parsing._
import com.alanrodas.fronttier.io._

import rapture.io._
import rapture.fs._
import rapture.fs.platform.adaptive
import rapture.core.strategy.throwExceptions

object FronttierConfigParser extends ConfigParser {
	def id : String = "fronttier"
	def name : String = "Fronttier"
	def fileName : String = "fronttier.ftt"

	def parse(fileName : String) = {
		val parsed = FronttierParsers.parseAll(
			FronttierParsers.configuration, fileName.asFile contents)
		parsed.fold[com.alanrodas.fronttier.Configuration]
			{ success => success.get }
			{ failure => throw new ParsingException(failure.msg) }
			{ error => throw new ParsingException(error.msg) }
	}

	private object FronttierParsers extends ExtendedRegexParsers {
		type ResultType = Configuration

		private def separator = whiteSpace.? ~> "::" <~ whiteSpace.?
		def urls = (url ~ whiteSpace.?).* ^^ {_.map{case (uri~_)=>uri} }

		private def repositoryKind = "[a-z]+".r ~ separator
		private def repository = repositoryKind.? ~ url ^^ { case maybeKind ~ uri =>
			Repository("", uri)
		}

		private def repositoriesSection = "repositories" ~
				followedBetweenBraces(list(repository, semiColonSeparator)) ^^ {case _~repos => repos}

		private def group : Parser[String] = "[a-zA-Z0-9]([a-zA-Z0-9-_.]*[a-zA-Z0-9])?".r
		private def name : Parser[String] = "[a-zA-Z0-9][a-zA-Z0-9-_]*".r
		private def version : Parser[String] = "[a-zA-Z0-9][a-zA-Z0-9-.]*".r

		private def dependency = group ~ separator ~ name ~ (separator ~> version).? ^^ { case group ~ _ ~ name ~ version =>
			Dependency(group, name, version)
		}

		private def DependenciesSection = "dependencies" ~
				followedBetweenBraces( list(dependency, semiColonSeparator) )  ^^ {case _ ~ deps => deps }

		private def configLeft = DependenciesSection ~ whiteSpace.? ~ repositoriesSection.? ^^ { case deps ~_~ maybeRepos =>
			com.alanrodas.fronttier.Configuration(maybeRepos.getOrElse(Nil), deps)
		}

		private def configRight = repositoriesSection.? ~ whiteSpace.? ~ DependenciesSection ^^ { case maybeRepos ~_~ deps =>
			com.alanrodas.fronttier.Configuration(maybeRepos.getOrElse(Nil), deps)
		}

		def configuration = discardWhites(configLeft | configRight) ^^ { value => value }
	}
}