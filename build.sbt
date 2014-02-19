name := "testScalaMongo"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)

play.Project.playScalaSettings

routesImport += "se.radley.plugin.salat.Binders._"
    
templatesImport += "org.bson.types.ObjectId"