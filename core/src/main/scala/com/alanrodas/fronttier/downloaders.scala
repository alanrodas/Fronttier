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

import com.alanrodas.fronttier.io._
import com.typesafe.scalalogging.slf4j.LazyLogging
import rapture.core.strategy.throwExceptions
import rapture.fs._
import rapture.io._
import rapture.net._


trait Downloader {
  def download(destination: FileUrl, url: String, dependency: Dependency)
    (implicit fronttier :Fronttier): Option[(Configuration, Seq[FronttierException])]
}

object HttpDownloader extends Downloader with LazyLogging {

  /*
  def download(destination: FileUrl, url: String): Boolean = {
    try {
      Http.parse(url) > (destination / url.split("/").last)
      true
    } catch {
      case e: java.io.FileNotFoundException => false
    }
  }
  */

  def download(destination: FileUrl, url: String, dependency: Dependency)
      (implicit fronttier :Fronttier): (Option[(Configuration, Seq[FronttierException])]) = {

    fronttier.configureLogger(logger)

    logger.info(s"Searching for $dependency declaration file")
    val remoteUrl = Http.parse(Http.parse(url).toString() + dependency.path)
    fronttier.parsers.map { parser =>
      val remoteDeclarationFile = remoteUrl / parser.fileName
      val localDeclarationFile = destination / parser.fileName
      try {
        remoteDeclarationFile > localDeclarationFile
         if (parser.existsFileAt(destination)) {
          logger.info("Found " + parser.name + " declaration file at: " + remoteDeclarationFile)
          val configuration = parser.parseAt(destination)
          logger.info("Downloading declared files:")
          val errors = configuration.files.foldLeft(Seq[FronttierException]()){ (sum, file) =>
            try {
              logger.info("    downloading: " + (remoteUrl / file))
              val folder = destination / file.substring(0, file.lastIndexOf("/"))
              if (!folder.exists) folder.mkdir(makeParents = true)
              (remoteUrl / file).>(destination / file)
              sum
            }
            catch { case e : java.io.FileNotFoundException =>
              logger.info("    download fail: " + e.getMessage)
              sum :+ UnavailableFileException(dependency, file)
            }
          }
          logger.info("===============")
          fronttier.download(configuration)
          localDeclarationFile.delete()
          Some((configuration, errors))
        } else None
      }
      catch { case e : java.io.FileNotFoundException =>
        None
      }
    }.find(_.isDefined).flatten
  }
}

object GitDownloader extends Downloader {
  def download(destination: FileUrl, url: String, dependency: Dependency)
      (implicit fronttier :Fronttier): Option[(Configuration, Seq[FronttierException])] = {
    None
  }
}

object SvnDownloader extends Downloader {
  def download(destination: FileUrl, url: String, dependency: Dependency)
      (implicit fronttier :Fronttier): Option[(Configuration, Seq[FronttierException])] = {
    None
  }
}