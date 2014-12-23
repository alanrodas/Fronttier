/*
 * Copyright 2014 Alan Rodas Bonjour
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
 */

package com.alanrodas.fronttier

class FronttierException(msg : String) extends RuntimeException(msg)

case class UnnavailableDependencyException(dep: Dependency, repos: Seq[Repository])
    extends FronttierException("The following dependency was not found: " + dep +
        "\nIn any of the following repositories:\n" + repos.mkString("\n"))

case class UnavailableFileException(dep : Dependency, filename : String)
    extends FronttierException("download fail for file: " + filename + " at dependency " + dep)

case class UnnavailableRepositoryException(repo: Repository)
    extends FronttierException("The following downloadable repository was not found: " + repo)

case class UninstalledDependencyException(dep: Dependency)
    extends FronttierException("The dependency " + dep + "is not installed.")

case class EmptyLocationException(location : String)
    extends FronttierException("There are no dependencies at " + location)