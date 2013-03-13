package controllers

import play.api._
import libs.iteratee._
import libs.json._
import play.api.mvc._

import libs.EventSource
import java.util.concurrent.TimeUnit
import play.libs.F.Promise

object EquationExecutor extends Controller {
  def stream = Action {
    Ok.feed(equationResultStream &> Concurrent.dropInputIfNotReady(50) &> EventSource()).as("text/event-stream")
  }

  case class EquationResult(x: Double, y: Double, changes: Set[String])
  implicit val equationResultFormat = Json.format[EquationResult]

  val (equationResultStream, _) = Concurrent.broadcast(
    Enumerator.generateM {
      Promise.timeout( {
        import models.Equation._
        val c = contributors
        clearContributors
        Some(Json.toJson(EquationResult(x, y, c)))
      },
      200, TimeUnit.MILLISECONDS ).getWrappedPromise
    }
  )
}
