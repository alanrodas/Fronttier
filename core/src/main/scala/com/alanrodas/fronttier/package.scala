package com.alanrodas


import com.alanrodas.fronttier.HttpDownloader._
import rapture.core.strategy.throwExceptions
import rapture.core.timeSystems.numeric
import rapture.fs._
import rapture.net._
import com.typesafe.scalalogging.slf4j.Logger

package object fronttier {

  def localRepository = System.getProperty("user.home") + "/.fronttier"

  def currentDirectory = System.getProperty("user.dir")

  implicit def stringToExtension(string: String): StringExtension = {
    StringExtension(string)
  }

  implicit def extensionToString(exs: StringExtension): String = {
    exs.string
  }

  implicit def httpToWrapper(url: HttpUrl): HttpUrlWrapper = {
    HttpUrlWrapper(url)
  }

  implicit def wrapperToHttp(wrapper: HttpUrlWrapper): HttpUrl = {
    wrapper.url
  }

  case class StringExtension(string: String) {
    def toFile = File / string
  }

  case class HttpUrlWrapper(url: HttpUrl) {
    def exists(timeout: Long = 0L) = url.get(timeout).status == 200

    def isResource = true

    def isCompressedResource = isZippedResource || isTaredResource

    def isTaredResource = true

    def isZippedResource = true

    def isRepositoryFolder = true

    def filename = ""
  }

  implicit class LevelLogger(logger : Logger) {
    def setAsInfo = logger.underlying.asInstanceOf[ch.qos.logback.classic.Logger]
        .setLevel(ch.qos.logback.classic.Level.INFO)
    def setAsWarn = logger.underlying.asInstanceOf[ch.qos.logback.classic.Logger]
        .setLevel(ch.qos.logback.classic.Level.WARN)
    def setAsError = logger.underlying.asInstanceOf[ch.qos.logback.classic.Logger]
        .setLevel(ch.qos.logback.classic.Level.ERROR)
    def setAsDebug = logger.underlying.asInstanceOf[ch.qos.logback.classic.Logger]
        .setLevel(ch.qos.logback.classic.Level.DEBUG)
  }
}