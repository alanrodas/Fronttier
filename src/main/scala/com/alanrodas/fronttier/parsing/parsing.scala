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
package com.alanrodas.fronttier.parsing

import scala.util.parsing.combinator.JavaTokenParsers

trait ExtendedRegexParsers extends JavaTokenParsers {

	override def skipWhitespace = false


	protected def EOL : Parser[String] = "\\r?\\n".r
	protected def EOF : Parser[String] = "\\z".r
	protected def emptyLine : Parser[String] = whiteSpace.? ~ EOL ^^ {_ => "\n" }

	// protected def NonEmptyChar = "[^\\s]".r

	protected def openBrace : Parser[String] = "\\{".r
	protected def closeBrace : Parser[String] = "\\}".r
	protected def openBracket : Parser[String] = "\\[".r
	protected def closeBracket : Parser[String] = "\\]".r
	protected def openParen : Parser[String] = "\\(".r
	protected def closeParen : Parser[String] ="\\)".r

	protected def natural = "[0-9]*".r
	protected def negative = "-[0-9]*".r

	protected def unicode = "\\P{M}\\p{M}*".r
	protected def unicodeWord = unicode.* ^^ {_.foldRight(""){(a, e) => a+e}}

	protected def word : Parser[String] = "\\w+".r
	protected def alphanumeric = "[a-zA-Z0-9]".r
	protected def token = "[a-zA-Z0-9-_.~]".r

	def fileName = """[^/?\*:;\{\}\\\s]+""".r

	protected def email = {
		val localChars = """[a-zA-Z0-9_-`/\'\!\|\&\#\+\*~\{\}]"""
		val local = s"$localChars((.$localChars)*$localChars)?"
		val server = "[a-zA-Z0-9-.]"
		s"$local@$server.[a-z]+(.[a-z]+)*".r
	}

	protected def url = {
		val prefix = "[a-z]+"
		val emailWithId = s"(([a-zA-Z0-9]+:)?)$email"
		val address = "[a-zA-Z0-9][a-zA-Z0-9-_.~]*"
		val port = ":[0-9]{1,5}"
		val argChars = """[0-9a-zA-Z\+\%@/&\[\];=_-]"""
		val path = """(/[a-zA-Z0-9-_.~()]*)*/?(\#?"""+argChars+"""*)?"""
		val args = """(\?"""+argChars+"""+)?"""

		s"$prefix:///?($emailWithId|$address)($port)?$path$args".r
	}

	protected def onLine[T](p : Parser[T]) : Parser[T] =
		whiteSpace.? ~ p ~ whiteSpace.? ~ EOL ^^ {
			case _~parser~_~_=> parser}

	protected def betweenBraces[T](p : Parser[T]) : Parser[T] =
		openBrace ~ whiteSpace.? ~ p ~ whiteSpace.? ~ closeBrace ^^ {case _~_~parser~_~_=> parser}

	protected def followedBetweenBraces[T](p : Parser[T]) : Parser[T] =
		whiteSpace.? ~ betweenBraces(p) ^^ {case _~parser => parser}

	protected def discardWhites[T](p : Parser[T]) : Parser[T] =
		whiteSpace.? ~ p ~ whiteSpace.? ~ EOF ^^ {case _~parser~_~_=> parser}

	protected def list[T](p : Parser[T], separator : Parser[Any]) : Parser[List[T]] =
		(p ~ separator).* ^^ {
			list => list.map {case parser ~ _ => parser}
		}

	protected def semiColonSeparator : Parser[String] =
		(whiteSpace.? ~ ";" ~ whiteSpace.? | whiteSpace.?) ^^(_=>";")

	implicit class ExtendedParseResult[R](result : ParseResult[R]) {

		def fold[T](onSuccess : Success[R] => T)
		           (onFailure : Failure => T)
		           (onError : Error => T) : T =
			result match {
				case s : Success[R] => onSuccess(s)
				case f : Failure => onFailure(f)
				case e : Error => onError(e)
			}
	}

	type ResultType <: Any
	// def parse(str : String) : ParseResult[ResultType]
}

class ParsingException(msg : String) extends RuntimeException(msg)
class FileNameDeclarationsException(msg : String) extends RuntimeException(msg)