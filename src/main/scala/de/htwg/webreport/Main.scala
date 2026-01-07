package de.htwg.webreport

import com.google.inject.Guice
import de.htwg.webreport.controller.sessionManager.SessionManagerTrait
import de.htwg.webreport.model.fileio.FileIOTrait
import de.htwg.webreport.aview.{Tui, Gui}
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.application.Platform
import scalafx.Includes._

object Main extends JFXApp3 {
  override def start(): Unit = {

    val injector = Guice.createInjector(new WebReportModule)
    val sessionManager = injector.getInstance(classOf[SessionManagerTrait])
    val fileIO = injector.getInstance(classOf[FileIOTrait])

    val tui = new Tui(sessionManager, fileIO)

    val tuiThread = new Thread(() => {
      tui.run()
    })
    tuiThread.setDaemon(true)
    tuiThread.start()

    if (System.getProperty("testMode") != "true") {
      val gui = new Gui(sessionManager)
      stage = new PrimaryStage {
        scene = gui.createScene()
        
        onCloseRequest = e => {
          System.exit(0)
        }
      }
    }
  }
}