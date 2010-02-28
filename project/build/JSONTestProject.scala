import sbt._

class JSONTestProject(info: ProjectInfo) extends DefaultProject(info) {
  val toolsConf = config("tools")

  val jsonLib = 
    "net.sf.json-lib" % "json-lib" % "2.2.3" % "compile" classifier "jdk15"
  val jsonSimple = 
    "com.googlecode.json-simple" % "json-simple" % "1.1"
  val scalaTest = 
    "org.scalatest" % "scalatest" % "1.0" % "test"
  val proguard =
    "net.sf.proguard" % "proguard" % "4.4" % "tools->default"

  val packageStandalone = task {packageStandaloneAction} dependsOn(compile)

  def packageStandaloneAction = {
    import FileUtilities._
    import java.util.jar.{Attributes, Manifest}

    val jarFile = defaultJarPath("-standalone.jar")
    withTemporaryDirectory(log) { work => 
      val workPath = Path.fromFile(work)
      (mainDependencies.scalaJars +++ runClasspath).get.foreach { file => 
        if (file.isDirectory) {
          copy(((file ##) ** "*").get, workPath, log)
        } else {
          unzip(file, workPath, -"META-INF/MANIFEST.MF", log)
        }
      }

      val manifest = new Manifest
      
      getMainClass(false) match {
        case None => ()
        case Some(cls) => 
          manifest.getMainAttributes.put(Attributes.Name.MAIN_CLASS, cls)
      }

      jar((workPath ##).get, jarFile, manifest, true, log)

      None
    }
  } 
}
