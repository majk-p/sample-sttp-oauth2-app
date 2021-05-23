ThisBuild / scalaVersion     := "2.13.6"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.0" cross CrossVersion.full)

val compilerOptions = Seq(
  scalacOptions ++= Seq(
    "-Ymacro-annotations"
  ),
  scalacOptions -= "-Xfatal-warnings",
  scalacOptions += "-Wconf:msg=\\$implicit\\$:s"
)

val Versions = new {
  val Cats = "2.3.0"
  val CatsEffect = "2.5.1"
  val Tapir = "0.18.0-M11"
  val Sttp = "3.2.3"
  val SttpOAuth2 = "0.9.0"
}

val Dependencies = new {

  private val cats = Seq(
    "org.typelevel" %% "cats-core" % Versions.Cats
  )

  private val catsEffect = Seq(
    "org.typelevel" %% "cats-effect-kernel" % Versions.CatsEffect
  )

  private val tapir = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-core" % Versions.Tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % Versions.Tapir 
  )

  private val sttp = Seq(
    "com.softwaremill.sttp.client3" %% "http4s-backend" % Versions.Sttp
  )

  private val sttpOAuth2 = Seq(
    "com.ocadotechnology" %% "sttp-oauth2" % Versions.SttpOAuth2
  )

  val appDependencies = 
    cats ++ catsEffect ++ tapir ++ sttp ++ sttpOAuth2
}

lazy val root = (project in file("."))
  .settings(
    name := "sttp-oauth2-sample",
    libraryDependencies ++= Dependencies.appDependencies,
    compilerOptions
  )
