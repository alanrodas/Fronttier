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


import java.io._
import rapture.core.strategy.throwExceptions
import rapture.fs.File
import rapture.fs.{FileStreamByteReader, _}
import rapture.fs.platform.adaptive
import rapture.io._

package object io {

  import rapture.fs.FileUrl

  implicit class TextFileUrl(val fileUrl: FileUrl) {
    def contents = {
      val bytes = fileUrl.slurp[Byte]
      new String(bytes, 0, bytes.length)
    }
    private def writeString(append : Boolean)( f : PrintWriter => Unit) {
      val writer = new PrintWriter(new BufferedWriter(new FileWriter(fileUrl.pathString, append)));
      f(writer)
      writer.flush()
      writer.close()
    }

    def create() = writeString(false)(writer => writer.write(""))
    def write(s : String) = writeString(false)(writer => writer.write(s))
    def append(s : String) = writeString(true)(writer => writer.append(s))
    def clear() = writeString(false)(writer => writer.write(""))
    def remove(start : Int, end : Int) = {
      val rest = contents.substring(0, start) + contents.substring(end)
      writeString(false)(writer => writer.write(rest))
    }
  }

  implicit class URLString(val str: String) {
    def asFile = {
      str match {
        case "~" => File.home
        case "." => File.currentDir
        case _ => File(new java.io.File(str))
      }
    }
  }

}
