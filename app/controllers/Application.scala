package controllers

import play.api.mvc._

object Application extends Controller {
  def index = Action { implicit request =>
    Ok(views.html.index())
  }
  def equation = Action { implicit request =>
    Ok(views.html.equation())
  }
}