package controllers.user

import play.api.mvc._
import play.api.libs.json._
import models._
import controllers.Actions._
import com.mongodb.casbah.WriteConcern
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formats._

object Users extends Controller {
  val registerForm: Form[User] = Form(
    mapping(
      "username" -> text(minLength = 4),
      // Create a tuple mapping for the password/confirm
      "password" -> tuple(
        "main" -> text(minLength = 6),
        "confirm" -> text).verifying(
          // Add an additional constraint: both passwords must match
          "Passwords don't match", passwords => passwords._1 == passwords._2),
      "sex" -> text,
      "age" -> number(min = 0, max = 100),
      "tel" -> text,
      "email" -> text,
      "education" -> text,
      "introduce" -> text,
      "added" -> date,
      "updated" -> date) {
        // Binding: Create a User from the mapping result (ignore the second password and the accept field)
        (username, password, email, sex, age, tel, education, introduce, added, _) => User(new ObjectId, username, password._1, email, sex, age, tel, education, introduce, added)
      } // Unbinding: Create the mapping values from an existing Hacker value
      {
        user => Some((user.username, (user.password, ""), user.sex, user.age, user.tel, user.email, user.education, user.introduce, user.added, user.updated))
      }.verifying(
        // Add an additional constraint: The username must not be taken (you could do an SQL request here)
        "This username is not available",
        username => !Seq("admin", "guest").contains(username)))

  val loginForm = Form(tuple(
    "username" -> nonEmptyText,
    "password" -> nonEmptyText))

  def index() = Action {
    val users = User.findAll().toList
    Ok(Json.toJson(users))
  }

  def login() = JsonAction[User] { user =>
    val u = User.authenticate(user.username, user.password).get
    Ok(views.html.success(u.username))
  }

  /*def register() = Action(parse.json) { implicit request =>
      request.body.validate[User].fold(
          valid = { user =>
         User.save(user, WriteConcern.Safe)
         Ok(views.html.success(user.username))
        },
        invalid = (e => BadRequest(JsError.toFlatJson(e)).as("application/json"))
      ) 
  }*/
  def register = Action {implicit request =>
    Users.registerForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index(errors)),
      {
       user => User.save(user, WriteConcern.Safe)
       Ok(views.html.success(user.username))
      })
  }
  
  def update(id: ObjectId) = JsonAction[User] { requestUser =>
    val user = requestUser.copy(id)
    User.save(user, WriteConcern.Safe)
    Ok(Json.toJson(user))
  }
  
  def delete(id: ObjectId) = JsonAction[User] { requestUser =>
    User.removeById(id)
    Ok("")
  }

  def view(id: ObjectId) = Action {
    User.findOneById(id).map { user =>
      Ok(Json.toJson(user))
    } getOrElse {
      NotFound
    }
  }
}