package controllers

import play.api.mvc._
import play.api._
import controllers.Actions._
import models._
import user.Users

object Application extends Controller{
	/*def login() = Action{
	  Ok(views.html.login(Users.loginForm))
	}*/
	
	def register() = Action{
	  Ok(views.html.index(Users.registerForm))
	}
	
	def login() = JsonAction[User] { user =>
    val u = User.authenticate(user.username, user.password).get
    Ok(views.html.success(u.username))
  }
}