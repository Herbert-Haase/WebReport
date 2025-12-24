package de.htwg.webreport.aview

import de.htwg.webreport.controller.sessionManager.SessionManagerTrait
import de.htwg.webreport.util.Observer
import scalafx.scene.Scene
import scalafx.application.Platform
import scalafx.scene.layout.{BorderPane, VBox, HBox, Priority, Region}
import scalafx.scene.control.{TextArea, TextField, Button, Label, ToolBar, Separator, ProgressBar, Tooltip}
import scalafx.scene.web.WebView
import scalafx.stage.FileChooser
import scalafx.geometry.Insets
import scalafx.Includes._
import javafx.concurrent.Worker
import javafx.beans.value.{ChangeListener, ObservableValue}
import scalafx.scene.web.WebView
import scala.compiletime.uninitialized

class Gui(sessionManager: SessionManagerTrait) extends Observer {
  sessionManager.add(this)

  private val famousLibs = Set("react", "angular", "vue", "svelte", "jquery", "bootstrap", "tailwind")
  private var parentStage: scalafx.stage.Window = uninitialized

  // -- UI Components --
  private lazy val webView = new WebView()
  private lazy val textArea = new TextArea { editable = false; styleClass += "code-area" }
  private lazy val urlField = new TextField {
    promptText = "http://..."
    hgrow = Priority.Always
    onAction = _ => {
      if (text.value.nonEmpty) {
        sessionManager.downloadFromUrl(text.value)
        text.value = ""
      }
    }
  }
  lazy val modeLabel = new Label(s"Storage: ${sessionManager.storageMode}") {
    style = "-fx-text-fill: #808080; -fx-padding: 0 0 0 10;"
  }
  
  private lazy val statusLabel = new Label("Welcome to WebScraper")
  private lazy val complexityLabel = new Label("Complexity: 0")
  private lazy val complexityBar = new ProgressBar() { prefWidth = 150 }
  
  private lazy val famousLibLabel = new Label("Libraries: None") {
    maxWidth = 400
    styleClass += "dashboard-text"
  }
  private lazy val detailStatsLabel = new Label("Images: 0 | Links: 0")

  // -- Toolbar --
  private lazy val mainToolbar = new ToolBar {
    content = List(
      new Button("📂 Open/Import") { onAction = _ => openFileChooser() },
      
      new Separator,
      urlField,
      new Button("⬇ Download") { 
        onAction = _ => {
          if (urlField.text.value.nonEmpty) {
            sessionManager.downloadFromUrl(urlField.text.value)
            urlField.text.value = ""
          }
        }
      },
      
      new Separator,
      new Button("↶") { onAction = _ => sessionManager.undo(); tooltip = new Tooltip("Undo") },
      new Button("↷") { onAction = _ => sessionManager.redo(); tooltip = new Tooltip("Redo") },
      
      new Separator,
      new Button("💾 Export Session") { 
        onAction = _ => exportSession() 
        tooltip = new Tooltip("Save cumulative history to XML/JSON")
      },
      
      new Region { hgrow = Priority.Always }, // Spacer
      
      new Button("Reset") {
        style = "-fx-background-color: #cdb91dff; -fx-text-fill: white;"
        onAction = _ => { sessionManager.reset(); urlField.text = "" }
      },
    )
  }

  private lazy val statsBar = new HBox(20) {
    padding = Insets(10)
    styleClass += "dashboard-bar"
    children = Seq(
      new VBox(2) { children = Seq(new Label("Health & Complexity") { styleClass += "dashboard-label-header" }, complexityBar, complexityLabel) },
      new Separator { orientation = scalafx.geometry.Orientation.Vertical },
      new VBox(2) { children = Seq(new Label("Web Anatomy") { styleClass += "dashboard-label-header" }, detailStatsLabel, famousLibLabel) }
    )
  }

  private lazy val mainLayout = new BorderPane {
    top = new VBox(mainToolbar, statsBar)
    center = textArea
    bottom = new HBox {
    padding = Insets(5)
    styleClass += "status-bar"
    children = Seq(statusLabel, new Region { hgrow = Priority.Always }, modeLabel)
  }
  }

  def createScene(): Scene = {
    val myScene = new Scene {
      root = mainLayout
      window.onChange { (_, _, newWindow) => if (newWindow != null) parentStage = newWindow }
    }
    val cssUrl = getClass.getResource("/style.css")
    if (cssUrl != null) myScene.stylesheets.add(cssUrl.toExternalForm)
    update(false)
    myScene
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
          sessionManager.downloadFromUrl(newValue)
        }
      }
    }
  })

  // --- File Operations ---
  
  private def openFileChooser(): Unit = {
    val fileChooser = new FileChooser()
    fileChooser.title = "Open File or Import Session"
    fileChooser.extensionFilters.addAll(
    new FileChooser.ExtensionFilter("All Supported", Seq("*.txt", "*.xml", "*.json")),
    new FileChooser.ExtensionFilter("WebScraper Session", Seq("*.xml", "*.json")),
    new FileChooser.ExtensionFilter("Text Files", "*.txt")
    )
    val selectedFile = fileChooser.showOpenDialog(parentStage)
    if (selectedFile != null) {
      sessionManager.loadFromFile(selectedFile.getAbsolutePath)
    }
  }

  private def exportSession(): Unit = {
    val fileChooser = new FileChooser()
    fileChooser.title = "Export Session History"
    fileChooser.extensionFilters.addAll(
      new FileChooser.ExtensionFilter("XML Data", "*.xml"),
      new FileChooser.ExtensionFilter("JSON Data", "*.json")
    )
    val file = fileChooser.showSaveDialog(parentStage)
    if (file != null) {
      sessionManager.saveSession(file.getAbsolutePath)
    }
  }

  // --- Update Logic ---
  override def update(isFilterUpdate: Boolean): Unit = {
    Platform.runLater {
      val d = sessionManager.data
      val content = d.displayLines.mkString("\n")

      // Status Bar
      val stats = s"Source: ${d.source} | Chars: ${d.characterCount} | Words: ${d.wordCount}"
      statusLabel.text = if(isFilterUpdate) s" [FILTER ACTIVE] $stats" else s" [READY] $stats"

      // Complexity Bar
      val progress = Math.min(d.complexity / 100.0, 1.0)
      complexityBar.progress = progress
      val color = if (d.complexity < 20) "green" else if (d.complexity < 60) "orange" else "red"
      complexityBar.style = s"-fx-accent: $color;"
      complexityLabel.text = s"Score: ${d.complexity}"

      detailStatsLabel.text = s"Images: ${d.imageCount} | Links: ${d.linkCount}"
      
      val visibleLibs = GuiLogic.getVisibleLibs(d.libraries, famousLibs)        
      famousLibLabel.text = "Famous Libs: " + (if (visibleLibs.isEmpty) "None" else visibleLibs.mkString(", "))
      if (d.libraries.nonEmpty) {
         famousLibLabel.tooltip = new Tooltip(d.libraries.mkString("\n"))
      }

      // Content View
      if (isHtml(content)) {
        if (d.source.startsWith("http")) webView.engine.loadContent(content) // simplified
        else webView.engine.loadContent(content)
        if (mainLayout.center.value != webView) mainLayout.center = webView
      } else {
        textArea.text = content
        if (mainLayout.center.value != textArea) mainLayout.center = textArea
      }
    }
  }

  private def isHtml(content: String): Boolean = {
    val c = content.trim.toLowerCase
    c.startsWith("<!doctype html") || c.startsWith("<html") || c.startsWith("<!--")
  }
}

// for testing
object GuiLogic {
  def getVisibleLibs(libs: List[String], famous: Set[String]): List[String] = {
    libs.filter(l => 
      famous.exists(f => l.toLowerCase.contains(f.toLowerCase))
    ).distinct.take(8)
  }
}