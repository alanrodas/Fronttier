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

import rapture.fs._
import rapture.fs.platform.adaptive
import com.alanrodas.fronttier.parsers._

object Fronttier {

	val DEFAULT_CONFIG_FORMAT_NAME =  "fronttier"
	val DEFAULT_CONFIG_FILENAME = "fronttier.ftt"
	val DEFAULT_CONFIG_PATH = "."

	val DefaultGlobalDir = File.home / ".ftt"
	val DefaultLocalDir = File.currentDir / ".ftt"

	val parsersMap = Map(
		FronttierConfigParser.id -> FronttierConfigParser,
		XmlConfigParser.id -> XmlConfigParser,
		XmlAttrConfigParser.id -> XmlAttrConfigParser
	)

	def parser(name : String) = parsersMap(name)

	def defaultFile(configParser : String) = {
		parsersMap.get(configParser).map(parser => parser.fileName)
	}

	def getCache(global : Boolean, local : Boolean,
	             nocache : Boolean, force : Boolean) = false

	def apply(configName : String, destination : String, cache :Cache = Cache.Global(false)) = {
		parser(configName).parse(defaultFile(configName).get).download(cache, destination)
	}
}

