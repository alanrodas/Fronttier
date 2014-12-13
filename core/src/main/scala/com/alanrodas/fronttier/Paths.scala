package com.alanrodas.fronttier

import rapture.fs._
import platform.adaptive

object Paths {
  val UserHomeDir = File.home / ".ftt"
  val DefaultWorkingDir = File.currentDir
  val ProjectHomeDir = File.currentDir / ".ftt"
  val CacheDir = UserHomeDir / "cache"
  val DatabaseFileName = ".fttdb"
}
