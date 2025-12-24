package de.htwg.webreport.aview

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.webreport.controller.sessionManager.impl1.SessionManager
import de.htwg.webreport.model.analyzer.impl1.SimpleAnalyzer
import de.htwg.webreport.model.webClient.impl1.SimpleWebClient
import de.htwg.webreport.model.fileio.implXML.XmlFileIO 
import javafx.embed.swing.JFXPanel
import scalafx.application.Platform
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class GuiSpec extends AnyWordSpec with Matchers {
  new JFXPanel()
  System.setProperty("glass.platform", "gtk")
  System.setProperty("java.awt.headless", "false") // JavaFX needs this to be false to even try to open a display


    "Gui" should {
      "be initializable and updateable" in {
        val latch = new CountDownLatch(1)
        var gui: Gui = null
        var error: Throwable = null

        Platform.runLater {
          try {
            val sm = new SessionManager(new SimpleAnalyzer(), new SimpleWebClient(), new XmlFileIO())
            gui = new Gui(sm)
            gui.update(false)
          } catch {
            case t: Throwable => error = t
          } finally {
            latch.countDown()
          }
        }

        latch.await(5, TimeUnit.SECONDS) should be(true)
        if (error != null) throw error
        gui should not be null
      }
    }

  "GuiLogic" should {
    val famous = Set("react", "angular", "vue", "scala")

    "filter and identify famous libraries from a list" in {
      val input = List("react-v18", "custom-lib", "de.htwg.Scraper", "angular-min")
      // Logic should find libraries containing the famous keywords
      val result = GuiLogic.getVisibleLibs(input, famous)
      
      result should contain allOf ("react-v18", "angular-min")
      result should not contain ("custom-lib")
    }

    "handle case insensitivity" in {
      val input = List("SCALA", "VueJS")
      val result = GuiLogic.getVisibleLibs(input, famous)
      result should contain allOf ("SCALA", "VueJS")
    }

    "limit display to 8 items to prevent UI overflow" in {
      val input = (1 to 10).map(i => s"react-$i").toList
      val result = GuiLogic.getVisibleLibs(input, famous)
      result.size should be <= 8
    }

    "return an empty list when no matches are found" in {
      GuiLogic.getVisibleLibs(List("unknown"), famous) should be (empty)
    }
  }
}
