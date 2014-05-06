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
import rapture.core.ParseException

trait Downloader {
	def download(from: String, to: FileUrl = localRepository.toFile)
}

object HttpDownloader extends Downloader {
	private def fetch(implicit from: HttpUrl, to: FileUrl) {
		val git = GIT clone ""
		git add ""
		git add ""
		git commit()


	}

	private def unzip()(implicit to: FileUrl) {

	}

	private def untar()(implicit to: FileUrl) {

	}

  override def download(from: String, to: FileUrl = localRepository.toFile) {
		Http.parse(from) match {
			case Left(remoteUrl : HttpUrl) => {
				remoteUrl match {
					case s if s.isZippedResource => {fetch; unzip}
					case s if s.isTaredResource => {fetch; untar}
					case s if s.isResource => {fetch}
					case s if s.isRepositoryFolder => {}
						case _ =>
				}
			}
			case Right(exception : ParseException) => exception
		}
  }
}

object GitDownloader extends Downloader {
	override def download(from: String, to: FileUrl = localRepository.toFile) {}
}

object SvnDownloader extends Downloader {
	override def download(from: String, to: FileUrl = localRepository.toFile) {}
}