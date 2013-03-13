package controllers

import play.api.libs.iteratee.Enumerator
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.mvc._


trait ExampleBase extends Controller {
  val enumerator = Enumerator("a", "bcd", "efgh")
  def resultsPage(title: String, results: Future[Int]) = Async {
    results.map { x => Ok(title + ":" + x) }
  }
}
