val scala3Version = "3.4.2" // Use a stable Scala version

lazy val root = project
  .in(file("."))
  .settings(
    name := "WebScraper",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    // You only need this one line for ScalaTest
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.14",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % Test,
    // coverageMinimumStmtTotal := 100,
    coverageExcludedFiles := ".*Main\\.scala",
  )
