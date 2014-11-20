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

import com.alanrodas.fronttier._
import scala.xml.XML

object XmlAttrConfigParser extends ConfigParser {
	def id : String = "xmlattr"
	def name : String = "XML Attribute"
	def fileName : String = "fronttier-ivy.xml"

	def parse(fileName : String) = {
		val xml = XML.loadFile(fileName)
		Configuration(
			xml \ "repositories" \ "repository" map {each =>
				Repository(each \ "@type" text, each \ "@url" text)},
			xml \ "dependencies" \ "dependency" map {each =>
				Dependency(each \ "@group" text, each \ "@name" text, each \ "@version" text) }
		)
	}
}