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
package com.alanrodas.fronttier.sbt

import com.alanrodas.fronttier.Fronttier
import sbt._
import Keys._
import com.typesafe.sbt.web.SbtWeb
import sbt.plugins.JvmPlugin

object FronttierPlugin extends AutoPlugin {

	override def requires = JvmPlugin
	override def trigger = allRequirements

	import autoImport._
	import SbtWeb.autoImport._

	object autoImport {
		val fronttier = taskKey[Unit]("Run fronttier in the current project")
		val fronttierDestination = settingKey[String]("The fronttier destination directory")

		def fronttierDefaults() : Seq[Def.Setting[_]] = Seq(
			fronttierDestination <<= fronttierDestination ?? "fronttierResources",
			fronttier := {FronttierRunner(fronttierDestination.value)}
			//(resourceManaged in Assets).value.absolutePath)
		)
	}

	override val projectSettings =
		inConfig(Compile)(fronttierDefaults()) ++
		inConfig(Test)(fronttierDefaults()) ++
		Seq(
			(fronttier in Compile) <<= (fronttier in Compile).triggeredBy(compile in Compile),
			(fronttier in Test) <<= (fronttier in Test).triggeredBy(compile in Test)
		)
}

object FronttierRunner {
	def apply(path :  String) = {
		println("TO INSTALL ON: " + path)
		//Fronttier(path)
	}
}