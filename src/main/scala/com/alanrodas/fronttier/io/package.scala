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
package com.alanrodas.fronttier

import com.alanrodas.fronttier.io._

import rapture.io._
import rapture.fs._
import rapture.fs.platform.adaptive
import rapture.core.strategy.throwExceptions
import rapture.fs.FileStreamByteReader

package object io {

	import rapture.fs.FileUrl

	implicit class TextFileUrl(val fileUrl : FileUrl) {
		def contents = {
			val bytes = fileUrl.slurp[Byte]
			new String(bytes, 0, bytes.length)
		}
	}

	implicit class URLString(val str : String) {
		def asFile = {
			str match {
				case "~" => File.home
				case "." => File.currentDir
				case _ => File(new java.io.File(str))
			}
		}
	}
}
