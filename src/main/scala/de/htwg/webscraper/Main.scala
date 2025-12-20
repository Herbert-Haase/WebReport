package de.htwg.webscraper

import com.google.inject.Guice
import de.htwg.webscraper.controller.ControllerInterface
import de.htwg.webscraper.controller.exporter.Exporter
import de.htwg.webscraper.aview.{Tui, Gui}
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage

object Main extends JFXApp3 {
  override def start(): Unit = {

    val injector = Guice.createInjector(new WebScraperModule)

    val controller = injector.getInstance(classOf[ControllerInterface])
    val exporter = injector.getInstance(classOf[Exporter])

    val tui = new Tui(controller, exporter)
    val gui = new Gui(controller, exporter)

    val tuiThread = new Thread(() => {
      tui.run()
    })
    tuiThread.setDaemon(true)
    tuiThread.start()

    stage = new PrimaryStage {
      title = "WebScraper Pro"
      width = 1100
      height = 800
      scene = gui.createScene()
    }
  }
}