package de.htwg.webscraper.aview

import de.htwg.webscraper.controller.{Controller, Observer}
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.scene.layout.{BorderPane, VBox, HBox, Priority}
import scalafx.scene.control.{TextArea, TextField, Button, Label, ToolBar, Separator}
import scalafx.scene.web.WebView
import scalafx.stage.FileChooser
import scalafx.geometry.Insets
import scalafx.Includes._
import scala.compiletime.uninitialized

class Gui(controller: Controller) extends Observer {
  controller.add(this)

  // Keep reference to the stage for file chooser
  private var parentStage: scalafx.stage.Window = uninitialized

  // -- Components --
  private val webView = new WebView()
  private val textArea = new TextArea {
    editable = false
    styleClass += "code-area"
  }
  
  private val pathField = new TextField { hgrow = Priority.Always; promptText = "File path..." }
  private val filterField = new TextField { 
    promptText = "Filter word..."; prefWidth = 150
    onAction = _ => controller.filter(text.value)
  }
  private val statusLabel = new Label("Welcome to WebScraper")
  private val urlField = new TextField { 
    promptText = "http://..."; prefWidth = 200
    onAction = _ => {
      val url = text.value
      if (url.nonEmpty) controller.downloadFromUrl(url)
    }
  }

  // -- Layout Definitions --
  
  private val mainToolbar = new ToolBar {
    content = List(
      new Button("📂 Open") { onAction = _ => openFileChooser() },
      new Separator,
      // new Label("URL:"),
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
      // new Label("Filter: "),
      filterField,
      new Button("Apply") { onAction = _ => controller.filter(filterField.text.value) },
      new Button("✖ Reset") { 
        style = "-fx-background-color: #cdb91dff;" 
        onAction = _ => {
          controller.reset()
          filterField.text = ""
          urlField.text = ""
        }
      },

      new Separator,
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
      // Store reference to window for file chooser
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