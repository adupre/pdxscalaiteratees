package controllers

import play.api._
import libs.iteratee._
import libs.json._
import play.api.mvc._

object Contribution {
  case class ControlCmd(variable: String, value: Double, by: String)
  implicit val controlCmdFormat = Json.format[ControlCmd]

  /*
   * Broadcast stream and channel creation
   * controlsStream: Enumerator, represents the output stream to be broadcasted
   * controlsChannel: Channel, same broadcast channel, different format. Enables imperative style interactions
   */
  val (controlsStream, controlsChannel) = Concurrent.broadcast[JsValue]

  /*
   * in: Incoming requests from uses
   * out: Outgoing responses. In this case, outgoing is broadcast only
   */
  def controls = WebSocket.using[JsValue] { implicit request =>
    val in = Enumeratee.collect[JsValue] { case o:JsObject => o } ><>
        Enumeratee.map[JsObject] { o => o.validate[ControlCmd]  } &>>
        Iteratee.foreach(_.map(handleCmd))

    (in, controlsStream)
  }

  def handleCmd(cmd: ControlCmd) {
    Logger.warn("Received " + cmd)
    val v = cmd.value

    import models.Equation._
    cmd.variable match {
      case "a" => a = v
      case "b" => b = v
      case "c" => c = v
      case "d" => d = v
    }
    contributors = contributors + cmd.by

    // dispatches out that the command was handled
    controlsChannel push Json.toJson(cmd)
  }
}
