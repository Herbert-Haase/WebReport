package de.htwg.webscraper.aview

import de.htwg.webscraper.controller.ControllerInterface
import de.htwg.webscraper.controller.exporter.Exporter
import de.htwg.webscraper.util.Observer
import scalafx.scene.Scene
import scalafx.application.Platform
import scalafx.scene.layout.{BorderPane, VBox, HBox, Priority, Region}
import scalafx.scene.control.{TextArea, TextField, Button, Label, ToolBar, Separator, ProgressBar}
import scalafx.scene.web.WebView
import scalafx.stage.FileChooser
import javafx.beans.value.{ChangeListener, ObservableValue}
import scalafx.geometry.Insets
import scalafx.Includes._
import javafx.concurrent.Worker
import scala.compiletime.uninitialized

class Gui(controller: ControllerInterface, exporter: Exporter) extends Observer {
  controller.add(this)

  // Filter for display logic
  private val famousLibs = Set("react", "angular", "vue", "svelte", "jquery", "bootstrap", "tailwind", "stacks", "pydata")

  private var parentStage: scalafx.stage.Window = uninitialized

  // -- UI Components --
  private val webView = new WebView()
  private val textArea = new TextArea {
    editable = false
    styleClass += "code-area"
  }
  val spacer = new Region { hgrow = Priority.Always }

  private val urlField = new TextField {
    promptText = "Enter URL (e.g., https://google.com)"
    hgrow = Priority.Always
    onAction = _ => controller.downloadFromUrl(text.value)
  }

  private val statusLabel = new Label("Ready")
  
  private val complexityLabel = new Label("Complexity: 0")
  private val complexityBar = new ProgressBar() { 
    prefWidth = 150
    style = "-fx-accent: green;" 
  }
  private val famousLibLabel = new Label("Libraries: None") {
  maxWidth = 600
  wrapText = false
  style = "-fx-text-overrun: ellipsis;"
  }
  private val detailStatsLabel = new Label("Images: 0 | Links: 0")

  private val mainLayout = new BorderPane {
    top = new VBox(0) {
      children = Seq(
        new ToolBar {
          items = Seq(
            new Button("Open File") { onAction = _ => openFileChooser() },
            new Separator(),
            new Label("URL:"),
            urlField,
            new Button("Download") { onAction = _ => controller.downloadFromUrl(urlField.text.value) },
            new Separator(),
            new Button("Export") { onAction = _ => exportData() },
            new Separator(),
            new Button("Undo") { onAction = _ => controller.undo() },
            new Button("Redo") { onAction = _ => controller.redo() },
            spacer,
            new Button("Reset") {
              style = "-fx-background-color: #cdb91dff; -fx-text-fill: white;"
              onAction = _ => {
                controller.reset()
                urlField.text = ""
              }
            },
            new Button("✖") {
              style = "-fx-background-color: #8b0000; -fx-text-fill: white;"
              onAction = _ => Platform.exit()
            }
          )
        },
        // Dashboard Header
        new HBox(20) {
          padding = Insets(10)
          styleClass += "dashboard-bar"
          children = Seq(
            new VBox(2) { 
              children = Seq(
                new Label("Complexity") { styleClass += "dashboard-label-header" }, 
                complexityBar, 
                complexityLabel
              ) 
            },
            new Separator { orientation = scalafx.geometry.Orientation.Vertical },
            new VBox(2) { 
              children = Seq(
                new Label("Web Anatomy") { styleClass += "dashboard-label-header" }, 
                detailStatsLabel, 
                famousLibLabel
              ) 
            }
          )
        }
      )
    }
    center = textArea
    bottom = new HBox {
    padding = Insets(5)
    children = Seq(statusLabel)
    styleClass += "status-bar"
}
  }
  // -- Web Engine Configuration for Navigation --
  webView.engine.getLoadWorker.stateProperty.addListener(new ChangeListener[Worker.State] {
    override def changed(observable: ObservableValue[? <: Worker.State], oldValue: Worker.State, newValue: Worker.State): Unit = {
      if (newValue == Worker.State.SUCCEEDED) {
      }
    }
  })

  // This listener intercepts link clicks in the WebView
  webView.engine.locationProperty.addListener(new ChangeListener[String] {
    override def changed(observable: ObservableValue[? <: String], oldValue: String, newValue: String): Unit = {
      if (newValue != null && newValue.nonEmpty) {
        urlField.text = newValue

        Platform.runLater {
          controller.downloadFromUrl(newValue)
        }
      }
    }
  })

  def createScene(): Scene = {
    val myScene = new Scene {
      root = mainLayout
      window.onChange { (_, _, newWindow) => if (newWindow != null) parentStage = newWindow }
      
      onKeyPressed = (event) => {
        if (event.controlDown) {
          event.code.name match {
            case "Z" => 
              controller.undo()
              event.consume()
            case "Y" => 
              controller.redo()
              event.consume()
            case _ =>
          }
        }
      }
    }
    
    val cssUrl = getClass.getResource("/style.css")
    if (cssUrl != null) myScene.stylesheets.add(cssUrl.toExternalForm)
    update(false)
    myScene
  }

  private def exportData(): Unit = {
    val fileChooser = new FileChooser()
    val file = fileChooser.showSaveDialog(parentStage)
    if (file != null) exporter.exportData(controller.data, file.getAbsolutePath)
  }

  private def openFileChooser(): Unit = {
    val fileChooser = new FileChooser()
    val selectedFile = fileChooser.showOpenDialog(parentStage)
    if (selectedFile != null) controller.loadFromFile(selectedFile.getAbsolutePath)
  }

  override def update(isFilterUpdate: Boolean): Unit = {
    Platform.runLater {
      val d = controller.data
      val content = d.displayLines.mkString("\n")
      
      val stats = s"Chars: ${d.characterCount} | Words: ${d.wordCount} | Lines: ${d.lineCount}"
      statusLabel.text = if(isFilterUpdate) s" [FILTER ACTIVE] $stats" else s" [READY] $stats"

      // Normalize score: 0-100 map to 0.0-1.0
      val progress = Math.min(d.complexity / 100.0, 1.0)
      complexityBar.progress = progress
      val color = if (d.complexity < 20) "green" else if (d.complexity < 60) "orange" else "red"
      complexityBar.style = s"-fx-accent: $color;"
      complexityLabel.text = s"Score: ${d.complexity}"

      detailStatsLabel.text = s"Images: ${d.images.length} | Links: ${d.links.length}"
      
      val visibleLibs = d.libraries.filter(l => famousLibs.exists(fl => l.toLowerCase.contains(fl))).distinct.take(6)
      val libSuffix = if (d.libraries.count(l => famousLibs.exists(fl => l.toLowerCase.contains(fl))) > 6) "..." else ""
      famousLibLabel.text = "Famous Libs: " + (if (visibleLibs.isEmpty) "None" else visibleLibs.mkString(", ") + libSuffix)

      if (isHtml(content)) {
        webView.engine.loadContent(content)
        if (mainLayout.center.value != webView) mainLayout.center = webView
      } else {
        textArea.text = content
        if (mainLayout.center.value != textArea) mainLayout.center = textArea
      }
    }
  }

  private def isHtml(content: String): Boolean = {
    val lower = content.toLowerCase.trim
    lower.startsWith("<!doctype html") || lower.startsWith("<html") || lower.startsWith("<!--")
  }
}