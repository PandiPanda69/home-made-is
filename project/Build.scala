import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "raspberry-bank"
  val appVersion      = "1.0-SNAPSHOT"

  val authName	      = "rpi-auth"
  val commonName      = "rpi-common"
  val homeName	      = "rpi-home"
  val torrentName     = "rpi-torrent"
  val fitnessName     = "rpi-fitness"

  val modulesPathName = "modules/"

  val appDependencies = Seq(
    javaCore,
    javaJdbc,
    javaJpa,
    "org.projectlombok" % "lombok" % "0.11.6",
    "org.xerial" % "sqlite-jdbc" % "3.7.2",
    "org.hibernate" % "hibernate-entitymanager" % "3.6.9.Final",
    "org.hibernate" % "hibernate-validator" % "4.0.0.GA",
    "org.hibernate.javax.persistence" % "hibernate-jpa-2.0-api" % "1.0.0.Final",
    "org.hibernate" % "hibernate" % "3.5.4-Final",
    "commons-collections" % "commons-collections" % "3.2.1",
    "org.springframework" % "spring-context" % "3.2.5.RELEASE",
    "org.springframework" % "spring-tx" % "3.2.5.RELEASE",
    "org.springframework" % "spring-orm" % "3.2.5.RELEASE"
  )


  lazy val common  = play.Project(commonName,  appVersion, appDependencies, path = file(modulesPathName + "common"))

  lazy val authentication = play.Project(authName, appVersion, appDependencies, path = file(modulesPathName + "authentication"))
  lazy val home    = play.Project(homeName,    appVersion, appDependencies, path = file(modulesPathName + "home"))
  lazy val torrent = play.Project(torrentName, appVersion, appDependencies, path = file(modulesPathName + "torrent"))
  lazy val fitness = play.Project(fitnessName, appVersion, appDependencies, path = file(modulesPathName + "fitness"))

  authentication.dependsOn(common)
  home.dependsOn(common)
  torrent.dependsOn(common)
  fitness.dependsOn(common)

  lazy val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )
  .dependsOn(common)
  .dependsOn(home).aggregate(home)
  .dependsOn(torrent).aggregate(torrent)
  .dependsOn(fitness).aggregate(fitness)
  .aggregate(authentication)

}
