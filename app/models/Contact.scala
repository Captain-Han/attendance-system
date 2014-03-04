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

case class Contact(
    id : ObjectId = new ObjectId,
  firstname: String,
  lastname: String,
  company: Option[String],
  informations: Seq[ContactInformation]
)

case class ContactInformation(
  label: String,
  email: Option[String],
  phones: List[String]
)

object Contact extends ContactDAO

trait ContactDAO extends ModelCompanion[Contact, ObjectId] {
  def collection = mongoCollection("contacts")
  val dao = new SalatDAO[Contact, ObjectId](collection) {}

  // Indexes
 // collection.ensureIndex(DBObject("username" -> 1), "username", unique = true)

  // Queries
  //def findOneByUsername(username: String): Option[Contact] = dao.findOne(MongoDBObject("username" -> username))
  //def findByEmail(email: String) = dao.find(MongoDBObject("email" -> email))
  //def authenticate(username: String, password: String): Option[Contact] = findOne(DBObject("username" -> username, "password" -> password))
}