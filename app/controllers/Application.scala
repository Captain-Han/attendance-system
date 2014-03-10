package controllers

import play.api.mvc._
import play.api._
import controllers.Actions._
import models._
import controllers.user.Users
import controllers.contact.Contacts
import se.radley.plugin.salat.Binders._
import se.radley.plugin.salat._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.gridfs.Imports._
import com.mongodb.casbah.gridfs.GridFS
import java.text.SimpleDateFormat
import play.api.libs.iteratee.Enumerator
import scala.concurrent.ExecutionContext

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
  
  def contact() = Action {
    Ok(views.html.contact.form(Contacts.contactForm()))
  }
  
  def imgs() = Action {
    Ok(views.html.image())
  }

  def upload = Action(parse.multipartFormData) { request =>
    request.body.file("photo") match {
      case Some(photo) =>
       val db = MongoConnection()("mydb")
       val gridFs = GridFS(db)
        val uploadedFile = gridFs.createFile(photo.ref.file)
        uploadedFile.contentType = photo.contentType.orNull
        uploadedFile.save()
        Ok("")
      case None => BadRequest("no photo")
    }
  }

  def getPhoto(file: ObjectId) = Action {
    import com.mongodb.casbah.Implicits._
    import ExecutionContext.Implicits.global
    
    val db = MongoConnection()("mydb")
    val gridFs = GridFS(db)

    gridFs.findOne(Map("_id" -> file)) match {
      case Some(f) => SimpleResult(
        ResponseHeader(OK, Map(
          CONTENT_LENGTH -> f.length.toString,
          CONTENT_TYPE -> f.contentType.getOrElse(BINARY),
          DATE -> new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", java.util.Locale.US).format(f.uploadDate)
        )),
        Enumerator.fromStream(f.inputStream)
      )

      case None => NotFound
    }
  }

}