package controllers

import play.api.mvc._
import play.api.libs.iteratee._

object Example3 extends ExampleBase {
  def index = Action { implicit request =>
    val iteratee: Iteratee[Int, Int] = Iteratee.fold(0)((acc, v) => acc + v)
    val newEnumerator = enumerator through Enumeratee.map(_.length)

    resultsPage("Example 3 results", newEnumerator run iteratee)
  }
}
