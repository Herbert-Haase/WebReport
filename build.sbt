val scala3Version = "3.4.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "WebScraper",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "org.scalactic" %% "scalactic" % "3.2.14",
      "org.scalatest" %% "scalatest" % "3.2.19" % Test,
      "org.scalafx" %% "scalafx" % "21.0.0-R32",
      "org.openjfx" % "javafx-controls" % "21.0.2" classifier "linux",
      "org.openjfx" % "javafx-graphics" % "21.0.2" classifier "linux",
      "org.openjfx" % "javafx-base" % "21.0.2" classifier "linux",
      "org.openjfx" % "javafx-media" % "21.0.2" classifier "linux",
      "org.openjfx" % "javafx-web" % "21.0.2" classifier "linux"
    ),

    coverageExcludedFiles := ".*Main\\.scala",

    fork := true,
    
    // CRITICAL FIX: Connects your terminal input to the forked process
    // This allows the TUI to read input instead of closing immediately.
    connectInput in run := true,

    // Suppress JavaFX warnings
    javaOptions ++= Seq(
      "-Dprism.verbose=false",
      "-Djavafx.verbose=false",
      // Removed the failing --add-opens line for javafx.graphics
      "--add-opens", "javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED"
    )
  )