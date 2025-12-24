package de.htwg.webreport

import com.google.inject.Guice
import de.htwg.webreport.controller.sessionManager.SessionManagerTrait
import de.htwg.webreport.model.fileio.FileIOTrait
import de.htwg.webreport.aview.{Tui, Gui}
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage

object Main extends JFXApp3 {
  override def start(): Unit = {

    val injector = Guice.createInjector(new WebScraperModule)

    val sessionManager = injector.getInstance(classOf[SessionManagerTrait])
    val fileIO = injector.getInstance(classOf[FileIOTrait])

    val tui = new Tui(sessionManager, fileIO)
    val gui = new Gui(sessionManager)

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