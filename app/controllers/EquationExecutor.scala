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

  /* Same as stream but spelling it out */
  def stream2 = Action {
    val e1: Enumerator[JsValue] = equationResultStream
    val e2: Enumerator[JsValue] = e1 through Concurrent.dropInputIfNotReady(50)
    val e3: Enumerator[String] = e2 through EventSource()
    Ok.feed(e3).as("text/event-stream")
  }

  case class EquationResult(x: Double, y: Double, changes: Set[String])
  implicit val equationResultFormat = Json.format[EquationResult]

  val equationResultStream = Enumerator.repeatM {
    Promise.timeout( {
      import models.Equation._
      val c = contributors
      clearContributors
      Json.toJson(EquationResult(x, y, c))
    },
    200, TimeUnit.MILLISECONDS ).getWrappedPromise
  }
}
