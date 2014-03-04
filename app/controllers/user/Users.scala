package controllers.user

import play.api.mvc._
import play.api.libs.json._
import models._
import java.util.Date
import controllers.Actions._
import com.mongodb.casbah.WriteConcern
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import play.api.data.Form
import play.api.data.Forms._

object Users extends Controller {
  val registerForm: Form[User] = Form(
    mapping(
      "username" -> text,
      // Create a tuple mapping for the password/confirm
      "password" -> tuple(
        "main" -> text,
        "confirm" -> text).verifying(
          // Add an additional constraint: both passwords must match
          "Passwords don't match", passwords => passwords._1 == passwords._2),
      "sex" -> text,
      "age" -> number,
      "tel" -> text,
      "email" -> text,
      "education" -> text,
      "introduce" -> text,
      "accept" -> checked("")) {
        // Binding: Create a User from the mapping result (ignore the second password and the accept field)
        (username, password, sex, age, tel, email, education, introduce, _) => User(new ObjectId, username, password._1, sex, age, tel, email,/* new Address(null,null,null,null,null) :: Nil, */education, introduce, new Date(), new Date())
      } // Unbinding: Create the mapping values from an existing Hacker value
      {
        user => Some((user.username, (user.password, ""), user.sex, user.age, user.tel, user.email, user.education, user.introduce, false))
      } /*.verifying(
        // Add an additional constraint: The username must not be taken (you could do an SQL request here)
        "This username is not available",
        username => !Seq("admin", "guest").contains(username)))*/ )
  val loginForm = Form(tuple(
    "username" -> nonEmptyText,
    "password" -> nonEmptyText).verifying(user => User.authenticate(user._1, user._2).nonEmpty)
    
  )

 val userForm: Form[User] = Form(
    mapping(
      "username" -> text,
      "password" -> text,
      "sex" -> text,
      "age" -> number,
      "tel" -> text,
      "email" -> text,
      "education" -> text,
      "introduce" -> text,
      "added" -> date,
      "updated" -> date) {
        // Binding: Create a User from the mapping result (ignore the second password and the accept field)
        (username, password, sex, age, tel, email, education, introduce, added, _) => User(new ObjectId, username, password, sex, age, tel, email, education, introduce, added, new Date())
      } // Unbinding: Create the mapping values from an existing Hacker value
      {
        user => Some((user.username, user.password, user.sex, user.age, user.tel, user.email, user.education, user.introduce, user.added, user.updated))
      }
  )
  
  def index() = Action {
    val users = User.findAll().toList
    Ok("")
  }

  /*def login() = JsonAction[User] { user =>
    val u = User.authenticate(user.username, user.password).get
    Ok(views.html.success(u.username))
  }*/

  def login() = Action { implicit request =>
    Users.loginForm.bindFromRequest.fold(
      errors => BadRequest(views.html.login(errors)),
      {
        user =>
          Ok(views.html.success(user._1))
      })
  }

  /* def register() = JsonAction[User] { user =>
    User.save(user, WriteConcern.Safe)
    Ok(Json.toJson(user))
    //Ok(views.html.success(user.username))
  }*/
  def register = Action { implicit request =>
    Users.registerForm.bindFromRequest.fold(
      errors => BadRequest(views.html.message1(errors)),
      {
        user =>
          User.save(user, WriteConcern.Safe)
          Ok(views.html.success(user.username))
      })
  }

  def update(id: ObjectId) = Action { implicit request =>
    Users.userForm.bindFromRequest.fold(
      errors => BadRequest(views.html.message1(errors)),
      {
        user =>
          User.save(user.copy(id = id), WriteConcern.Safe)
          Ok(views.html.success(user.username))
      })
  }

  def show(username: String) = Action {
    User.findOneByUsername(username).map { user =>
      val userForm = Users.userForm.fill(user)
      Ok(views.html.UserInfomation(userForm, user))
    } getOrElse {
      NotFound
    }
  }
}