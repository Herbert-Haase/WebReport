val scala3Version = "3.4.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "WebReport",
    version := "1.0.0",
    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "org.scalactic" %% "scalactic" % "3.2.14",
      "org.scalatest" %% "scalatest" % "3.2.19" % Test,
      "org.scalafx" %% "scalafx" % "21.0.0-R32",
      "org.openjfx" % "javafx-controls" % "21.0.2" classifier "linux",
      "org.openjfx" % "javafx-graphics" % "21.0.2" classifier "linux",
      "org.openjfx" % "javafx-base" % "21.0.2" classifier "linux",
      "org.openjfx" % "javafx-media" % "21.0.2" classifier "linux",
      "org.openjfx" % "javafx-web" % "21.0.2" classifier "linux",
      "com.google.inject" % "guice" % "7.0.0",
      "org.scala-lang.modules" %% "scala-xml" % "2.2.0",
      "com.typesafe.play" %% "play-json" % "2.10.4",
      "org.scala-lang.modules" %% "scala-xml" % "2.2.0",
      "com.typesafe.play" %% "play-json" % "2.10.4"
    ),

    coverageExcludedPackages := "<empty>;de\\.htwg\\.webreport\\.WebReportModule;.*Main.*",
    coverageExcludedFiles := ".*WebReportModule.*;.*Main.*",

    fork := true,
    
    connectInput in run := true,

    // Suppress JavaFX warnings
    javaOptions ++= Seq(
      "-Dprism.verbose=false",
      "-Djavafx.verbose=false",
      // Removed the failing --add-opens line for javafx.graphics
      "--add-opens", "javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED"
    )
  )
