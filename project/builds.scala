
import sbt._
import Keys._
import sbtassembly.Plugin.AssemblyKeys._
import sbtassembly.Plugin._

object V {
  val scaldingVersion = "0.11.2"
}

object Builds extends Build {

  def isReadme(fileName: String): Boolean = {
    val ReadMe = """(readme|about)([.]\w+)?$""".r
    fileName.toLowerCase match {
      case ReadMe(_, ext) if ext != ".class" => true
      case _ => false
    }
  }

  def isLicenseFile(fileName: String): Boolean = {
    val LicenseFile = """(license|licence|notice|copying)([.]\w+)?$""".r
    fileName.toLowerCase match {
      case LicenseFile(_, ext) if ext != ".class" => true // DISLIKE
      case _ => false
    }
  }

  lazy val sbtAssemblySettings = assemblySettings ++ Seq(

    excludedJars in assembly <<= (fullClasspath in assembly) map { cp =>
      val excludes = Set(
        "jsp-api-2.1-6.1.14.jar",
        "jsp-2.1-6.1.14.jar"
      )
      cp filter { jar => excludes(jar.data.getName) }
    },

    mergeStrategy in assembly <<= (mergeStrategy in assembly) {
      (old) => {
        case PathList(ps @ _*) if isReadme(ps.last) || isLicenseFile(ps.last) => MergeStrategy.rename
        case "META-INF/NOTICE.txt" => MergeStrategy.discard
        case "META-INF/LICENSE.txt" => MergeStrategy.discard
        case PathList("META-INF", xs @ _*) =>
          (xs map {_.toLowerCase}) match {
            case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) => MergeStrategy.discard
            case ("license" :: Nil) => MergeStrategy.first
            case _ => MergeStrategy.discard
          }
        case _ => MergeStrategy.first
      }
    }

  )

  lazy val buildSettings = Defaults.defaultSettings ++ sbtAssemblySettings ++ Seq(
    version := "1.0",
    organization := "com",
    scalaVersion := "2.10.4"
  )

  lazy val app = Project("cascading-project-template", file("."), settings = buildSettings)
    .settings(
      libraryDependencies ++= Seq(
        "com.twitter" %  "scalding-core_2.10" % V.scaldingVersion,
        "com.twitter" %  "scalding-commons_2.10" % V.scaldingVersion,
        "com.twitter" %  "scalding-args_2.10" % V.scaldingVersion
      )
    )
}