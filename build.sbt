import Dependencies._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "org.paulkore"
ThisBuild / organizationName := "paulkore"

lazy val root = (project in file("."))
  .settings(
    name := "hacker-news-analyzer",
    libraryDependencies += scalaTest % Test
  )
