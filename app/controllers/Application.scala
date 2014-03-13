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
import java.io.FileInputStream
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.Iterator
import javax.imageio.ImageIO
import javax.imageio.ImageReadParam
import javax.imageio.ImageReader
import javax.imageio.stream.ImageInputStream
import java.io.ByteArrayOutputStream
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.FileOutputStream

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
    Ok(views.html.displayImage())
    //Ok(views.html.image))
  }

  def upload_file = Action(parse.multipartFormData) { request =>
    request.body.file("photo") match {
      case Some(photo) =>
       val db = MongoConnection()("mydb")
       val gridFs = GridFS(db)
        val uploadedFile = gridFs.createFile(photo.ref.file)
        uploadedFile.contentType = photo.contentType.orNull
        uploadedFile.save()
        print(uploadedFile._id.toString())
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
  
  
  def upload_tmp = Action(parse.multipartFormData) { 
    import com.mongodb.casbah.Implicits._
    import ExecutionContext.Implicits.global
    request =>
    request.body.file("photo") match {
      case Some(photo) =>
      
        print("123123")
        photo.ref.file.createNewFile();
        
        Ok("");
        
        /*val inStream = new FileInputStream(photo.ref.file)
        //得到图片的二进制数据，以二进制封装得到数据，具有通用性  
        val iis = ImageIO.createImageInputStream(inStream);  
        iis.`
        //new一个文件对象用来保存图片，默认保存当前工程根目录  
        val imageFile = new File("BeautyGirl.jpg");  
        //创建输出流  
        val outStream = new FileOutputStream(imageFile);  
        //写入数据  
        outStream.write(data);  
        //关闭输出流  
        outStream.close();  
*/
      case None => NotFound
    }
  }
  
  /*def upload = Action(parse.multipartFormData) { request =>
    request.body.file("photo") match {
      case Some(photo) =>
        val is = new FileInputStream(photo.ref.file)
          
            // ImageReader声称能够解码指定格式  
            val it = ImageIO.getImageReadersByFormatName(photo.contentType.get);  
            val reader = it.next();  
  
            // 获取图片流  
            val iis = ImageIO.createImageInputStream(is);  
  
            // 输入源中的图像将只按顺序读取  
            reader.setInput(iis, true);  
  
            // 描述如何对流进行解码  
            val param = reader.getDefaultReadParam();  
  
            // 图片裁剪区域  
            val rect = new Rectangle(x, y, width, height);  
  
            // 提供一个 BufferedImage，将其用作解码像素数据的目标  
            param.setSourceRegion(rect);  
  
            // 使用所提供的 ImageReadParam 读取通过索引 imageIndex 指定的对象  
            val bi = reader.read(0, param);  
            
            bi.flush();
        
            val bs = new ByteArrayOutputStream();   
          
    
    val imOut = ImageIO.createImageOutputStream(bs);  
              
            ImageIO.write(bi, "png", imOut);  
              
            val newis= new ByteArrayInputStream(bs.toByteArray());  
        
       
        
        
        
        
        
        
        
        
        
        
        
        
        
       val db = MongoConnection()("mydb")
       val gridFs = GridFS(db)
        val uploadedFile =  gridFs.createFile(newis)
        uploadedFile.filename_=("0920210107")
        uploadedFile.contentType = photo.contentType.orNull
        uploadedFile.save()
        Ok("")
      case None => BadRequest("no photo")
    }
  }
  */
  

}