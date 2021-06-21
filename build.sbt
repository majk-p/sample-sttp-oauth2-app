ThisBuild / scalaVersion     := "2.13.6"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "net.michalp"
ThisBuild / organizationName := "majk-p"

addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.0" cross CrossVersion.full)
addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")

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
  val Circe = "0.14.1"
  val Tapir = "0.18.0-M15"
  val Sttp = "3.3.6"
  val SttpOAuth2 = "0.10.0"
  val GithubGraphQL = "0.9.5-2"
}

val Dependencies = new {

  private val cats = Seq(
    "org.typelevel" %% "cats-core" % Versions.Cats
  )

  private val catsEffect = Seq(
    "org.typelevel" %% "cats-effect" % Versions.CatsEffect
  )

  private val circe = Seq(
    "io.circe" %% "circe-generic" % Versions.Circe
  )

  private val tapir = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-core" % Versions.Tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % Versions.Tapir 
  )

  private val sttp = Seq(
    "com.softwaremill.sttp.client3" %% "async-http-client-backend-cats-ce2" % Versions.Sttp
  )

  private val sttpOAuth2 = Seq(
    "com.ocadotechnology" %% "sttp-oauth2" % Versions.SttpOAuth2
  )

  private val githubGraphQL = Seq(
    "io.github.er1c" %% "caliban-github-api-client" % Versions.GithubGraphQL
  )

  val appDependencies = 
    cats ++ catsEffect ++ circe ++ tapir ++ sttp ++ sttpOAuth2 ++ githubGraphQL
}

lazy val root = (project in file("."))
  .settings(
    name := "sttp-oauth2-sample",
    libraryDependencies ++= Dependencies.appDependencies,
    compilerOptions
  )
