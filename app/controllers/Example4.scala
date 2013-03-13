package controllers

import play.api.mvc._
import play.api.libs.iteratee._

object Example4 extends ExampleBase {
  /* Same as Example 3, with operators instead of named methods */
  def index = Action { implicit request =>
    val iteratee: Iteratee[Int, Int] = Iteratee.fold(0)((acc, v) => acc + v)
    val newEnumerator = enumerator &> Enumeratee.map(_.length)

    resultsPage("Example 4 results", newEnumerator |>>> iteratee)
  }
}
