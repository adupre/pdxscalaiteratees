package controllers

import play.api.mvc._
import play.api.libs.iteratee._
import scala.concurrent.ExecutionContext.Implicits.global

object Examples  extends Controller {
  def example1 = Action { implicit request =>
    val iter1: Iteratee[String, Int] = {
      def step(acc: Int, input: Input[String]): Iteratee[String, Int] = {
        input match {
          case Input.EOF | Input.Empty => Done(acc, Input.EOF)
          case Input.El(chunk) => Cont[String, Int](input => step(acc + chunk.length, input))
        }
      }
      Cont[String, Int](input => step(0, input))
    }
    val iter1R = Enumerator("a", "bcd", "efgh") run iter1

    Async {
      iter1R.map { x => Ok("" + x) }
    }
  }

  def example2 = Action { implicit request =>
    val iter1: Iteratee[String, Int] = Iteratee.fold(0)((acc, v) => acc + v.length)
    val iter1R = Enumerator("a", "bcd", "efgh") run iter1

    Async {
      iter1R.map { x => Ok("" + x) }
    }
  }
}
