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

import com.alanrodas.fronttier.parsers._
import com.alanrodas.fronttier.io._
import rapture.fs._
import platform.adaptive
import com.typesafe.config._
import rapture.core.strategy.throwExceptions
import scala.collection.JavaConversions._

case class FronttierDatabase(val location : FileUrl) {
  if (!location.exists) location.mkdir(makeParents = true)
  private val path = location / Paths.DatabaseFileName
  if (!path.exists) {
    path.create()
  }

  private var conf = ConfigFactory.parseFile(path.javaFile)

  implicit class QuotableString(str : String) {
    def quoted = "\"" + str + "\""
  }

  private def update(): Unit = {
    ConfigFactory.invalidateCaches()
    conf = ConfigFactory.parseFile(path.javaFile)
  }

  def save(config : Configuration) : FronttierDatabase = {
    if (!isInstalled(config.currentDependency)) {
      path.append(config.currentDependency.toString.quoted + " {\n" +
          "  dependencies: " +
          ( if (config.dependencies.isEmpty) "[]"
            else config.dependencies.map(_.toString.quoted).mkString("[\n    ", ",\n    ", "\n  ]") ) +
          "\n" +
          "  files: " +
          ( if (config.files.isEmpty) "[]"
            else config.files.map(_.quoted).mkString("[\n    ", ",\n    ", "\n  ]") )+
          "\n}\n"
      )
    }
    update()
    this
  }

  // def save(dependency : Dependency) = ???

  def delete(dependency : Dependency) = {
    if (isInstalled(dependency)) {
      val contents = path.contents
      val start = contents.indexOf(dependency.toString.quoted + " {")
      val end = contents.indexOf("}", start) + 1
      path.remove(start, end)
    }
    update()
    this
  }

  def delete(config : Configuration) : FronttierDatabase = {
    if (isInstalled(config.currentDependency)) {
      val contents = path.contents
      val start = contents.indexOf(config.currentDependency.toString.quoted + "\\s{")
      val end = contents.indexOf("}", start) + 1
      path.remove(start, end)
    }
    update()
    this
  }

  def clear() = {
    path.clear()
    update()
  }

  def dependencyTree: DependencyTree = {
    DependencyTree(roots.map{dep => makeDependencyNode(dep)})
  }

  private def makeDependencyNode(dependency : Dependency) : DependencyNode = {
    DependencyNode(dependency, dependencies(dependency).map{e => makeDependencyNode(e)})
  }

  def inverseDependencyTree: DependencyTree = {
    DependencyTree(independent.map{dep => makeDependeeNode(dep, Nil) })
  }

  private def makeDependeeNode(dependency : Dependency, currentDependees : Seq[Dependency]) : DependencyNode = {
    val thisDependees = dependees(dependency)
    val eachTransitiveDependees = (for (dep <- thisDependees) yield transitiveDependees(dep)).flatten
    val addableDependees = thisDependees.filterNot(each => eachTransitiveDependees.contains(each))
    DependencyNode(dependency, addableDependees.map{e => makeDependeeNode(e, currentDependees ++ addableDependees)})
  }

  def installed : Set[Dependency] = conf.root().map{each =>
    val splitted = each._1.split("::").map(_.trim)
    Dependency(splitted(0), splitted(1), splitted(2))
  }.toSet

  def isInstalled(dependency : Dependency) : Boolean = {
    conf.hasPath(dependency.toString.quoted)
  }

  def files(dependency : Dependency) : Set[String] = {
    conf.getList(dependency.toString.quoted + ".files").unwrapped().map(_.asInstanceOf[String]).toSet
  }

  def dependencies(dependency : Dependency) : Set[Dependency] = {
    conf.getList(dependency.toString.quoted + ".dependencies").unwrapped().map{each =>
      val string = each.asInstanceOf[String]
      val splitted = string.split("::").map(_.trim)
      Dependency(splitted(0), splitted(1), splitted(2))
    }.toSet
  }

  def transitiveDependencies(dependency : Dependency) =
    dependencies(dependency).foldLeft[Set[Dependency]](Set[Dependency]()){ (set, elem) =>
      set + elem ++ dependencies(elem)
    }

  def dependees(dependency : Dependency) : Set[Dependency] = {
    installed.filter{ each => dependencies(each).contains(dependency) }
  }

  def transitiveDependees(dependency : Dependency) =
    dependees(dependency).foldLeft[Set[Dependency]](Set[Dependency]()){ (set, elem) =>
      set + elem ++ dependees(elem)
    }

  def roots = {
    installed.filter(dependees(_).isEmpty)
  }

  def independent = {
    installed.filter(each => dependencies(each).isEmpty)
  }

  def deleteThis() = {
    path.delete()
  }
}

case class DependencyNode(dependency: Dependency, dependencies : Set[DependencyNode]) {

  override def toString : String = toString(2)

  def toString(spaces : Int) : String = {
    dependency.toString + (if (dependencies.nonEmpty)
        "\n" + List.fill(spaces)(" ").mkString +
            dependencies.map(_.toString(spaces+2)).mkString("|- ", "\n" + List.fill(spaces)(" ").mkString + "|- ", "")
        else "")
  }

  def hasDependencies = dependencies.nonEmpty

  def flatten : Seq[Dependency] = Seq(dependency) ++ dependencies.map(_.flatten).flatten
}

case class DependencyTree(nodes : Set[DependencyNode]) {
  override def toString = nodes.mkString("\n")
  def flatten : Seq[Dependency] = nodes.toSeq.map(_.flatten).flatten
}