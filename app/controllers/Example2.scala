package controllers

import play.api.mvc._
import play.api.libs.iteratee._

object Example2 extends ExampleBase {
  def index = Action { implicit request =>
    val iteratee: Iteratee[String, Int] = Iteratee.fold(0)((acc, v) => acc + v.length)

    resultsPage("Example 2 results", enumerator run iteratee)
  }
}
