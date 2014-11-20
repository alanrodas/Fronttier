/**
 * All files in this project are distributed using
 * an Apache 2.0 licence which you may find at
 *
 * http://opensource.org/licenses/Apache-2.0
 *
 * Created by: Alan Rodas Bonjour
 * Last modification: 03/05/14 at 23:35
 */
package com.alanrodas.fronttier

import rapture.fs._
import rapture.net._
import rapture.io._
import rapture.core.strategy.throwExceptions
import rapture.fs.platform.adaptive

/*
object Fronttier extends App {
	val remoteUri = localRepository.toFile
	val current = currentDirectory.toFile
	println(remoteUri)
	println(current)
	println(remoteUri.exists)


	def apply()(implicit configuration : FronttierConfiguration) = {}
	def apply(filename : String)(implicit configuration : FronttierConfiguration) = {}
	def apply(dependency : Dependency)(implicit configuration : FronttierConfiguration) = {}
	def apply(dependencies : List[Dependency])(implicit configuration : FronttierConfiguration) = {}

}

class FronttierConfiguration {
	val repositories : List[Repository] = Nil
	val verbose = false
	val destination = "."
	val cache = Some(GLOBAL_CACHE_PATH)
	val pluginsCache = Some(GLOBAL_PLUGIN_CACHE_PATH)
	val parsers = Map[String, ConfigurationParser]()
}

trait ConfigurationParser {
	val id = "fronttier"
	val defaultName = "fronttier.ftt"
	val defaultExtension = "ftt"

	def parse(filename : Some[String]) : (List[Repository], List[Dependency]) = {return null}

}

case class Repository(val url : String) {}
case class Dependency(val company : String, val name : String, val version : Some[String] = None)
*/