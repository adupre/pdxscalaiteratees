package controllers

import play.api.mvc._
import play.api.libs.iteratee._
import annotation.tailrec

object Example1 extends ExampleBase {
  /* Example of fold implementation */
  def index = Action { implicit request =>
    @tailrec
    val iteratee: Iteratee[String, Int] = {
      def step(acc: Int, input: Input[String]): Iteratee[String, Int] = {
        input match {
          case Input.EOF | Input.Empty => Done(acc, Input.EOF)
          case Input.El(chunk) => Cont[String, Int](input => step(acc + chunk.length, input))
        }
      }
      Cont[String, Int](input => step(0, input))
    }

    resultsPage("Example 1 results", enumerator run iteratee)
  }
}
