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
import rapture.core.ParseException
import com.alanrodas.fronttier.io._

trait Downloader {
	def download(destination : String, url : String, dependency : Dependency) : Boolean
}

object HttpDownloader extends Downloader {

	def download(destination: String, url : String) : Boolean = {
		try {
			Http.parse(url) > (destination.asFile / url.split("/").last)
			true
		} catch {
			case e : java.io.FileNotFoundException => false
		}
	}

	def download(destination : String, url : String, dependency : Dependency) : Boolean = {
		implicit var remoteLocation : HttpUrl = dependency.remoteLocation(Http.parse(url))
		implicit var localDestination : FileUrl = destination.asFile
		val remoteDeclarationFile = dependency.remoteDeclarationFile
		val localDeclarationFile = dependency.declarationFile
		try {
			remoteDeclarationFile > localDeclarationFile
			for (file <- dependency.files) {
				remoteLocation / file > (localDestination / file)
			}
			true
		} catch {
			case e : java.io.FileNotFoundException => false
		}
	}
}

object GitDownloader extends Downloader {
	//override def download(from: String, to: FileUrl = localRepository.toFile) {}

	def download(destination : String, url : String, dependency : Dependency) = {
		false
	}
}

object SvnDownloader extends Downloader {
	//override def download(from: String, to: FileUrl = localRepository.toFile) {}

	def download(destination : String, url : String, dependency : Dependency) = {
		false
	}
}