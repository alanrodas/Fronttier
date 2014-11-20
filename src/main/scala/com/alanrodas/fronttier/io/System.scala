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
package com.alanrodas.fronttier.io

object System {
	val OS = java.lang.System.getProperty("os.name")
	val isWindows = OS.toLowerCase.contains("win")
	val isMac = OS.toLowerCase.contains("mac")
	val isSolaris = OS.toLowerCase.contains("sunos")
	val isUnix = OS.toLowerCase.contains("nix") || OS.toLowerCase.contains("nux") || OS.toLowerCase.contains("aix")

	def exit(status : Int = 0) = java.lang.System.exit(status)
}
