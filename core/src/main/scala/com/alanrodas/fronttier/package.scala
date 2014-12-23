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

package com.alanrodas

import com.alanrodas.fronttier.HttpDownloader._
import rapture.core.strategy.throwExceptions
import rapture.core.timeSystems.numeric
import rapture.fs._
import rapture.net._
import com.alanrodas.scaland.logging._
import rapture.fs.platform.adaptive

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
    def toFile = string.head match {
      case '~' => File.home / string.tail
      case '/' => File / string.tail
      case _ => File.currentDir / string
    }
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
}