package com.alanrodas

import rapture.fs._
import rapture.net._
import rapture.io._
import rapture.core.strategy.throwExceptions
import rapture.fs.platform.adaptive
import rapture.uri.Url
import rapture.core.timeSystems.javaUtil

package object fronttier {

	def localRepository = System.getProperty("user.home") + "/.fronttier"
	def currentDirectory = System.getProperty("user.dir")

	case class StringExtension(string : String) {
		def toFile = File / string
	}

	implicit def stringToExtension(string : String) : StringExtension = { StringExtension(string) }
	implicit def extensionToString(exs : StringExtension) : String = { exs.string }


	case class HttpUrlWrapper(url : HttpUrl) {
		def exists(timeout : Long = 0L) = url.get(timeout).status == 200
		def isResource = true
		def isTaredResource = true
		def isZippedResource = true
		def isCompressedResource = isZippedResource || isTaredResource
		def isRepositoryFolder = true
		def filename = ""
	}

	implicit def httpToWrapper(url : HttpUrl) : HttpUrlWrapper = { HttpUrlWrapper(url) }
	implicit def wrapperToHttp(wrapper : HttpUrlWrapper) : HttpUrl = { wrapper.url }
}