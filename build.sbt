ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "org.paulkore"
ThisBuild / organizationName := "paulkore"

lazy val root = (project in file("."))
  .settings(
    name := "hacker-news-analyzer",
    libraryDependencies ++= Seq(
      // core dependencies
      "org.skinny-framework" %% "skinny-http-client" % "3.0.0",
      "org.skinny-framework" %% "skinny-json" % "3.0.0",
      // test dependencies
      "org.scalatest" %% "scalatest" % "3.0.5" % Test
    )
  )
