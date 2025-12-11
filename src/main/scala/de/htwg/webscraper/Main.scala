package de.htwg.webscraper

import de.htwg.webscraper.controller.Controller
import de.htwg.webscraper.aview.{Tui, Gui}
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene

object Main extends JFXApp3 {
  override def start(): Unit = {
    val controller = new Controller()
    val tui = new Tui(controller)
    val gui = new Gui(controller)

    // Run TUI in background thread
    val tuiThread = new Thread(() => {
      tui.run()
    })
    tuiThread.setDaemon(true) // Ensures TUI dies when GUI closes
    tuiThread.start()

    // Initialize the GUI with the primary stage from JFXApp3
    stage = new PrimaryStage {
      title = "WebScraper Pro"
      width = 1100
      height = 800
      scene = gui.createScene()
    }
  }
}