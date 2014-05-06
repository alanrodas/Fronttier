/**
 * All files in this project are distributed using
 * an Apache 2.0 licence which you may find at
 *
 * http://opensource.org/licenses/Apache-2.0
 *
 * Created by: alanrodas
 * Last modification: 04/05/14 at 03:07
 */
package com.alanrodas.fronttier

import rapture.fs._
import rapture.core.strategy.throwExceptions
import rapture.fs.platform.adaptive

class PackageTests extends UnitSpec {

	"A String that represents a local filesystem path" should "Parse to it's correspondent rapture file when toFile is invoked" in {
		"/temp/testfile".toFile should equal (File / "/temp/testfile")
	}

	it should "" in {

	}
}
