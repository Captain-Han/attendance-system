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
                 //adds: Address,
                 education: String,
                 introduce: String,
                 added: Date = new Date(),
                 updated: Date = new Date()
                 )

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