package controllers.contact

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import views._
import models._
import com.mongodb.casbah._
import se.radley.plugin.salat.Binders._

object Contacts extends Controller {
  
  /**
   * Contact Form definition.
   */
  def contactForm(id: ObjectId = new ObjectId) = Form(
    
    // Defines a mapping that will handle Contact values
    mapping(
        "id" -> ignored(id),
      "firstname" -> nonEmptyText,
      "lastname" -> nonEmptyText,
      "company" -> optional(text),
      
      // Defines a repeated mapping
      "informations" -> seq(
        mapping(
          "label" -> nonEmptyText,
          "email" -> optional(email),
          "phones" -> list(
            text verifying pattern("""[0-9.+]+""".r, error="A valid phone number is required")
          ) 
        )(ContactInformation.apply)(ContactInformation.unapply)
      )
      
    )(Contact.apply)(Contact.unapply)
  )
  
  /**
   * Display a form pre-filled with an existing Contact.
   */
  
  /**
   * Handle form submission.
   */
  def submit = Action { implicit request =>
    contactForm().bindFromRequest.fold(
      errors => BadRequest(html.contact.form(errors)),
      {
      contact => 
        Contact.save(contact, WriteConcern.Safe)
        Ok(html.contact.summary(contact))
      }
    )
    
   
  }
  
   def update(id: ObjectId) = Action { implicit request =>
    contactForm().bindFromRequest.fold(
      errors => BadRequest(html.contact.edit(errors,id)),
      {
        contact =>
          Contact.save(contact.copy(id = id), WriteConcern.Safe)
          Ok(html.contact.edit(contactForm().fill(contact),id))
      })
  }
  
   
   def contact() = Action {
    val id = new ObjectId("531449caeae5657fa2e9a24a")
    Ok(views.html.contact.edit(Contacts.contactForm().fill(Contact.findOneByID(id).get), id))
  }
  
}