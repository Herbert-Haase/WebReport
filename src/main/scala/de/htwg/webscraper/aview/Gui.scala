package de.htwg.webscraper.aview

import de.htwg.webscraper.controller.ControllerInterface
import de.htwg.webscraper.util.Observer
import scalafx.scene.Scene
import scalafx.application.Platform
import scalafx.scene.layout.{BorderPane, VBox, HBox, Priority, Region} 
import scalafx.scene.control.{TextArea, TextField, Button, Label, ToolBar, Separator}
import scalafx.scene.web.WebView
import scalafx.stage.FileChooser
import scalafx.geometry.Insets
import scalafx.Includes._
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.concurrent.Worker
import scala.compiletime.uninitialized

class Gui(controller: ControllerInterface) extends Observer {
  controller.add(this)

  private var parentStage: scalafx.stage.Window = uninitialized

  // -- Components --
  private val webView = new WebView()
  private val textArea = new TextArea {
    editable = false
    styleClass += "code-area"
  }
  
  private val pathField = new TextField { hgrow = Priority.Always; promptText = "File path..." }
  private val filterField = new TextField { 
    promptText = "Filter word..."; 
    onAction = _ => controller.filter(text.value)
  }
  private val statusLabel = new Label("Welcome to WebScraper")
  private val urlField = new TextField { 
    promptText = "http://..."; 
    onAction = _ => {
      val url = text.value
      if (url.nonEmpty) controller.downloadFromUrl(url)
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

  // -- Layout Definitions --
  
  private val mainToolbar = new ToolBar {
      val spacer = new Region { hgrow = Priority.Always }
      content = List(
      new Button("📂 Open") { onAction = _ => openFileChooser() },
      urlField,
      new Button("⬇ Download") { 
        onAction = _ => {
          val url = urlField.text.value
          if (url.nonEmpty) controller.downloadFromUrl(url)
        }
      },
      new Separator,
      new Button("↶ Undo") { onAction = _ => controller.undo() },
      new Button("↷ Redo") { onAction = _ => controller.redo() },
      new Separator,
      filterField,
      new Button("Apply") { onAction = _ => controller.filter(filterField.text.value) },

      new Separator,
      spacer,
      new Button("Reset") { 
        style = "-fx-background-color: #cdb91dff;" 
        onAction = _ => {
          controller.reset()
          filterField.text = ""
          urlField.text = ""
        }
      },

      new Button("✖") { 
        style = "-fx-background-color: #8b0000;" 
        onAction = _ => Platform.exit()
      }
      )
  }

  private val statusBar = new HBox {
    styleClass += "status-bar"
    children = statusLabel
    padding = Insets(5)
  }

  private val mainLayout = new BorderPane {
    top = new VBox(mainToolbar)
    center = textArea
    bottom = statusBar
  }

  // -- Create Scene Method --
  def createScene(): Scene = {
    val myScene = new Scene {
      root = mainLayout
      window.onChange { (_, _, newWindow) =>
        if (newWindow != null) parentStage = newWindow
      }
    }

    val cssUrl = getClass.getResource("/style.css")
    if (cssUrl != null) myScene.stylesheets.add(cssUrl.toExternalForm)

    // Trigger initial update
    update(false)
    
    myScene
  }

  private def openFileChooser(): Unit = {
    val fileChooser = new FileChooser()
    fileChooser.title = "Open Resource File"
    val selectedFile = fileChooser.showOpenDialog(parentStage)
    if (selectedFile != null) {
      controller.loadFromFile(selectedFile.getAbsolutePath)
    }
  }

  override def update(isFilterUpdate: Boolean): Unit = {
    Platform.runLater {
      val content = controller.data.displayLines.mkString("\n")
      val stats = s"Chars: ${controller.data.characterCount} | Words: ${controller.data.wordCount}"
      statusLabel.text = if(isFilterUpdate) s" [FILTER ACTIVE] $stats" else s" [READY] $stats"

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
    val c = content.trim.toLowerCase
    c.startsWith("<!doctype html") || c.startsWith("<html")
  }
}