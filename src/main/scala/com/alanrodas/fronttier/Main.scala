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

import com.alanrodas.scaliapp._
import com.alanrodas.scaliapp.core.runtime.Command
import com.alanrodas.fronttier.io.Terminal
import com.alanrodas.fronttier.parsing._
import com.alanrodas.fronttier.io._

object Main extends CLIApp {

	private class CommandRunner(val commandCall : Command) {
		private val local = commandCall flag "local"
		private val global = commandCall flag "global"
		private val nocache = commandCall flag "nocache"
		private val force = commandCall flag "force"

		val cache = {
			require(!(global.defined && local.defined),
				"Global and local cannot be defined at the same time")
			require(!(nocache.defined && (global.defined || local.defined)),
				"If nocache is define, global and local cannot not be defined")
			if (nocache.value) {
				new NoCache(force.value)
			} else {
				if (local.defined) Cache.Local(force.value)
				else Cache.Global(force.value)
			}
		}
		val verbose = commandCall flagValue "verbose"
		val fileName : String = {
			val fn = commandCall argument "filename"
			val config = commandCall argument "config"
			if (config.defined) Fronttier.defaultFile(config.values.head).getOrElse(fn.values.head)
			else fn.values.head
		}
		val configParser = (commandCall argumentValue "config").head
		val destination = (commandCall argumentValue "destination").head
		val plugins = commandCall argumentValue "load"

		def execute(executor : (CommandRunner, Terminal) => Unit ) = {
			val terminal = Terminal(verbose)
			try {
				executor(this, terminal)
			} catch {
				case e : java.util.NoSuchElementException =>
					terminal.error(s"The configuration format $configParser is not valid.")
				case e : java.io.FileNotFoundException =>
					terminal.error(s"The configuration file $fileName was not found.")
				case e : ParsingException =>
					terminal.error(s"The configuration file $fileName has errors.\n"+e.getMessage)
				case e : UnnavailableDependencyException =>
					terminal.error(e.getMessage)
				case e : FileNameDeclarationsException =>
					terminal.error(e.getMessage)
				case e : java.lang.IllegalArgumentException =>
					terminal.error(e.getMessage)
				case e : Exception => if (e.getMessage != null)
					terminal.error(e.getMessage) else "An unknown error has ocurred"
			}
		}
	}

	root accepts Seq(
		flag named "global" alias "g" as false,
		flag named "local" alias "l" as false,
		flag named "nocache" alias "n" as false,
		flag named "force" alias "f" as false,
		flag named "verbose" alias "v" as false,
		arg named "filename" alias "F" taking 1 as List("fronttier.ftt"),
		arg named "config" alias "c" taking 1 as List("fronttier"),
		arg named "destination" alias "d" taking 1 as List("."),
		arg named "load" alias "L" taking 10 values
	) receives Seq() does { commandCall =>
		new CommandRunner(commandCall).execute { (config, terminal) =>
			val (configParser, fileName, destination, cache) =
				(config.configParser, config.fileName, config.destination, config.cache)
			terminal.info(s"Fetching $configParser as a configuration.")
			val file = Fronttier.parser(configParser)
			terminal.info(s"Opening $fileName as a $configParser configuration file.")
			val configuration = file.parse(config fileName)
			terminal.info(s"The following configuration was found:")
			//terminal.info(configuration)
			terminal.info("repositories:")
			configuration.repositories.foreach{each => terminal.info("    " + each) }
			terminal.info("dependencies:")
			configuration.dependencies.foreach{each => terminal.info("    " + each) }
			cache match {
				case nc : NoCache => terminal.info("Not using cache" +
						(if (nc.force) " and forcing downloads" else ""))
				case fbc : FolderBasedCache => terminal.info("Using " + fbc.uri.pathString +
						" for saving downloads" +
						(if (fbc.force) " and forcing downloads" else ""))
			}
			terminal.info(s"Saving to " + destination.asFile.pathString)
			configuration.download(cache, destination)(terminal)
			//if ("  ".matches("""[\s]""") ) println("SUCCESS")
			//else println("FAILURE")
		}
	}

	command named "install" accepts Seq(
		flag named "global" alias "g" as false,
		flag named "local" alias "l" as false,
		flag named "nocache" alias "n" as false,
		flag named "force" alias "f" as false,
		flag named "verbose" alias "v" as false,
		arg named "destination" alias "d" taking 1 as List(".")
	) minimumOf 1 does { commandCall =>
		new CommandRunner(commandCall).execute { (config, terminal) =>
			// Fronttier.parser(config configParser).parse(config fileName)
		}
	}

	command named "cache" accepts Seq(
		flag named "global" alias "g" as false,
		flag named "local" alias "l" as false,
		flag named "nocache" alias "n" as false,
		flag named "force" alias "f" as false,
		flag named "verbose" alias "v" as false
	) minimumOf 1 does { commandCall =>
		new CommandRunner(commandCall).execute { (config, terminal) =>
			// Fronttier.parser(config configParser).parse(config fileName)
		}
	}

	command named "uncache" accepts Seq(
		flag named "global" alias "g" as false,
		flag named "local" alias "l" as false,
		flag named "nocache" alias "n" as false,
		flag named "force" alias "f" as false,
		flag named "verbose" alias "v" as false
	) minimumOf 1 does { commandCall =>
		new CommandRunner(commandCall).execute { (config, terminal) =>
			// Fronttier.parser(config configParser).parse(config fileName)
		}
	}

	command named "plug" accepts Seq(
		flag named "global" alias "g" as false,
		flag named "local" alias "l" as false,
		flag named "force" alias "f" as false,
		flag named "verbose" alias "v" as false
	) minimumOf 1 does { commandCall =>
		new CommandRunner(commandCall).execute { (config, terminal) =>
			// Fronttier.parser(config configParser).parse(config fileName)
		}
	}

	command named "unplug" accepts Seq(
		flag named "global" alias "g" as false,
		flag named "local" alias "l" as false,
		flag named "force" alias "f" as false,
		flag named "verbose" alias "v" as false
	) minimumOf 1 does { commandCall =>
		new CommandRunner(commandCall).execute { (config, terminal) =>
			// Fronttier.parser(config configParser).parse(config fileName)
		}
	}
}
