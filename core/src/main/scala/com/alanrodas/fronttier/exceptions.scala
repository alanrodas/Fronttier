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