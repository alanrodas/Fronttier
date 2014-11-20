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

case class Terminal(val verboseEnabled : Boolean) {
	private def output(msg : Any) = println(msg)
	private def outputWithColor(color : String, msg : Any) = output(color + msg.toString + Console.RESET)

	def info(msg : Any) : Unit = if (verboseEnabled) output(msg.toString)
	def info(bool : Boolean, msgIfTrue : Any, msgIfFalse : Any) : Unit = if (bool) info(msgIfTrue) else info(msgIfFalse)
	def info(bool : Boolean, msgIfTrue : Any) : Unit = if (bool) info(msgIfTrue)
	def show(msg : Any) = output(msg.toString)
	def warning(msg : Any) = outputWithColor(Console.YELLOW,  msg)
	def error(msg : Any) = outputWithColor(Console.RED,  msg)
	def success(msg : Any) = outputWithColor(Console.GREEN, msg)
}
