package models

import play.api.Play.current
import java.util.Date
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import se.radley.plugin.salat.Binders._
import mongoContext._
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class User(
				 id: ObjectId = new ObjectId,	
                 username: String,
                 password: String,
                 sex: String,
                 age: Int,
                 tel: String,
                 email: String,
                 education: String,
                 introduce: String,
                 added: Date = new Date(),
                 updated: Date = new Date()
                 )

//object User extends UserDAO with UserJson

object User extends UserDAO

trait UserDAO extends ModelCompanion[User, ObjectId] {
  def collection = mongoCollection("users")
  val dao = new SalatDAO[User, ObjectId](collection) {}

  // Indexes
  collection.ensureIndex(DBObject("username" -> 1), "username", unique = true)

  // Queries
  def findOneByUsername(username: String): Option[User] = dao.findOne(MongoDBObject("username" -> username))
  def findByEmail(email: String) = dao.find(MongoDBObject("email" -> email))
  def authenticate(username: String, password: String): Option[User] = findOne(DBObject("username" -> username, "password" -> password))
}

/**
 * Trait used to convert to and from json
 *//*
trait UserJson {

  implicit val userJsonWrite = new Writes[User] {
    def writes(u: User): JsValue = {
      Json.obj(
        "id" -> u.id,
        "username" -> u.username,
        "passsword" -> u.password,
        "sex" -> u.sex,
        "tel" -> u.tel,
        "age" -> u.age,
        "email" ->u.email,
        "education" -> u.education,
        "introduce" -> u.introduce,
        "added" -> u.added,
        "updated" -> u.updated
      )
    }
  }
  implicit val userJsonRead = (
    (__ \ 'id).read[ObjectId] ~
    (__ \ 'username).read[String] ~
    (__ \ 'password).read[String] ~
    (__ \ 'sex).read[String] ~
    (__ \ 'age).read[Int] ~
    (__ \ 'tel).read[String] ~
    (__ \ 'email).read[String] ~
    (__ \ 'education).read[String] ~
    (__ \ 'introduce).read[String] ~
    (__ \ 'added).read[Date] ~
    (__ \ 'updated).read[Date]
  )(User.apply _)
}*/