/*
 * Copyright 2014 Alan Rodas Bonjour
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
 */

package com.alanrodas.fronttier.sbt

import com.alanrodas.fronttier._
import _root_.sbt._
import Keys._
import _root_.sbt.plugins.JvmPlugin

object FronttierPlugin extends AutoPlugin {

	override def requires = JvmPlugin
	override def trigger = allRequirements

	import autoImport._
//	import SbtWeb.autoImport._

	object autoImport {
		val fronttier = taskKey[Unit]("Run fronttier in the current project")
		val fronttierClean = taskKey[Unit]("Run fronttier clean in the current project")
		val fronttierDestination = settingKey[String]("The fronttier destination directory")
		val fronttierForce = settingKey[Boolean]("Force fronttier")
		val fronttierVerbose = settingKey[Boolean]("Enable fronttier verbose mode")
		val fronttierRunAfter = settingKey[Boolean]("Enable fronttier to run after compile")
		val fronttierRunBefore = settingKey[Boolean]("Enable fronttier to run before compile")

		def fronttierDefaults() : Seq[Def.Setting[_]] = Seq(
			fronttierDestination <<= fronttierDestination ?? "fronttierResources",
			fronttierForce <<= fronttierForce ?? false,
			fronttierVerbose <<= fronttierVerbose ?? false,
			fronttierRunAfter <<= fronttierRunAfter ?? false,
			fronttierRunBefore <<= fronttierRunBefore ?? false,
			fronttier := {FronttierRunner(fronttierDestination.value, fronttierVerbose.value, fronttierForce.value)},
			fronttierClean := {FronttierRunner.clean(fronttierDestination.value, fronttierVerbose.value)}
		)
	}

	override val projectSettings =
		inConfig(Compile)(fronttierDefaults()) ++
		inConfig(Test)(fronttierDefaults()) ++
		Seq(
			(compile in Compile) := {
				(fronttier in Compile).value
				(compile in Compile).value
			},
			(compile in Test) := {
				(fronttier in Test).value
				(compile in Test).value
			}
		) ++ Seq(
			clean := {
				(fronttierClean in Compile).value
				clean.value
			}
		)
}

object FronttierRunner {
	def apply(path :  String, verbose : Boolean, force : Boolean) {
		val configuration = FronttierConfiguration(
			verbose = verbose,
			destination = path,
			force = force,
			useCache = true
		)
		import scala.concurrent._
		import scala.concurrent.duration._
		import scala.concurrent.ExecutionContext.Implicits.global
		val fronttier = Fronttier(configuration)
		val f = Future {fronttier.downloadRoot()}
		Await.result(f, 1.5.minutes)
	}

	def clean(path :  String, verbose : Boolean): Unit = {
		val configuration = FronttierConfiguration(
			verbose = verbose,
			destination = path,
			force = true,
			useCache = true
		)
		Fronttier(configuration).deleteAll()
	}
}