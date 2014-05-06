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

object Fronttier extends App {
	val remoteUri = localRepository.toFile
	val current = currentDirectory.toFile
	println(remoteUri)
	println(current)
	println(remoteUri.exists)
}
