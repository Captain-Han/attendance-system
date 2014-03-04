package controllers

import play.api.mvc._
import play.api._
import controllers.Actions._
import models._
import controllers.user.Users
import controllers.contact.Contacts

object Application extends Controller {

  def index() = Action {
    Redirect(routes.Application.login)
  }

  def login() = Action {
    Ok(views.html.login(Users.loginForm))
  }

  def register() = Action {
    Ok(views.html.register(Users.registerForm))
  }
  
  
  
}