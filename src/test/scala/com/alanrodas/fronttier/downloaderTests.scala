/**
 * All files in this project are distributed using
 * an Apache 2.0 licence which you may find at
 *
 * http://opensource.org/licenses/Apache-2.0
 *
 * Created by: Alan Rodas Bonjour
 * Last modification: 03/05/14 at 23:41
 */
package com.alanrodas.fronttier

import rapture.net._
import rapture.fs._
import rapture.fs.platform.adaptive
import com.alanrodas.fronttier.HttpDownloader

class HttpDownloaderTest extends UnitSpec {

	"An HttpDownloader" should
			"donwload file correctly when given a valid URL" in {
				val remoteUri = Http / "alanrodas.github.io" / "docs" / "alanrodas-cv-es.pdf"
				val localRepo = localRepository.toFile / ".test"

				assume(remoteUri.exists())

				HttpDownloader.download("http://alanrodas.github.io/docs/alanrodas-cv-es.pdf", localRepo)
				val resultFile = localRepo / "alanrodas-cv-es.pdf"

				assert(resultFile.exists)
			}

			it should "download file and unzip it if the URL references a ZIP file" in {
				assert(true)
			}

			it should "throw an Throw a ArtifactNotFoundException if the URL is invalid" in {
				assert(true)
			}
}
