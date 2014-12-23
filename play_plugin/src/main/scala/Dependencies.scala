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

package controllers

import com.alanrodas.fronttier.Fronttier
import play.twirl.api.Html

import play.api.mvc.Call

class ReverseAssets {
  def at(path: String): Call = {
    new Call("GET", "/assets/" + path)
  }
}

case class ResourceFile(val name : String, val reverseAsset : ReverseAssets) {
  def isCss = name.endsWith(".css")
  def isJs = name.endsWith(".js")
  def toHtml(properties : Map[String, String] = Map()) = {
    if (isCss) Html("<link rel=\"stylesheet\" href=\"" + reverseAsset.at(name).url + "\">")
    else if (isJs) Html("<script src=\"" + reverseAsset.at(name).url + "\"></script>")
    else Html("")
  }
}

object Dependencies {
  def apply(directory : String, filter : String) : Html = {
    Html(files(directory, filter).map(_.toHtml()).foldLeft(""){(total, each) =>
      total + each.toString()
    })
  }

  def apply(filter : String) : Html = {
    apply("public", filter)
  }

  def apply() : Html = {
    apply("public", "")
  }

  def files(directory : String, filter : String) = {
    val fronttier = Fronttier(directory)
    (filter.toList match {
      case Nil => {fronttier.fileList}
      case '*' :: rest => {fronttier.fileList.filter{_.endsWith(rest.mkString)}}
      case '~' :: '*' :: tail => {fronttier.fileList.filterNot{_.endsWith(tail.mkString)}}
      case '-' :: '*' :: tail => {fronttier.fileList.filterNot{_.endsWith(tail.mkString)}}
      case _ => {fronttier.fileList.filter{_.contains(filter)}}
    }).map(ResourceFile(_, new ReverseAssets()))
  }
}

/*
import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.Play.current
import scala.util.matching.Regex
import play.api.Play
import scala.collection.JavaConverters._


class WebJarAssets(assetsBuilder: AssetsBuilder) extends Controller {

  val WebjarFilterExprDefault = """.*"""
  val WebjarFilterExprProp = "org.webjars.play.webJarFilterExpr"

  val webJarFilterExpr = current.configuration.getString(WebjarFilterExprProp).getOrElse(WebjarFilterExprDefault)

  val webJarAssetLocator = new WebJarAssetLocator(
    WebJarAssetLocator.getFullPathIndex(
      new Regex(webJarFilterExpr).pattern, Play.application.classloader))

  /**
   * Controller Method to serve a WebJar asset
   *
   * @param file the file to serve
   * @return the Action that serves the file
   */
  def at(file: String): Action[AnyContent] = {
    assetsBuilder.at("/" + WebJarAssetLocator.WEBJARS_PATH_PREFIX, file)
  }

  /**
   * Locate a file in a WebJar
   *
   * @example Passing in `jquery.min.js` will return `jquery/1.8.2/jquery.min.js` assuming the jquery WebJar version 1.8.2 is on the classpath
   *
   * @param file the file or partial path to find
   * @return the path to the file (sans-the webjars prefix)
   *
   */
  def locate(file: String): String = {
    webJarAssetLocator.getFullPath(file).stripPrefix(WebJarAssetLocator.WEBJARS_PATH_PREFIX + "/")
  }

  /**
   * Locate a file in a WebJar
   *
   * @param webjar the WebJar artifactId
   * @param file the file or partial path to find
   * @return the path to the file (sans-the webjars prefix)
   *
   */
  def locate(webjar: String, file: String): String = {
    webJarAssetLocator.getFullPath(webjar, file).stripPrefix(WebJarAssetLocator.WEBJARS_PATH_PREFIX + "/")
  }

}

object WebJarAssets extends WebJarAssets(Assets)
*/