package de.htwg.webreport.aview

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.webreport.controller.sessionManager.impl1.SessionManager
import de.htwg.webreport.model.analyzer.impl1.SimpleAnalyzer
import de.htwg.webreport.model.webClient.impl1.SimpleWebClient
import de.htwg.webreport.model.fileio.implXML.XmlFileIO 
import de.htwg.webreport.model.data.impl1.Data
import javafx.embed.swing.JFXPanel
import scalafx.application.Platform
import scalafx.stage.FileChooser
import java.io.File
import java.util.concurrent.{CountDownLatch, TimeUnit}

class GuiSpec extends AnyWordSpec with Matchers {
  new JFXPanel() 

  class TestGui(sm: SessionManager) extends Gui(sm) {
    var fileToReturn: File = null
    override protected def showOpenDialog(fc: FileChooser): File = fileToReturn
    override protected def showSaveDialog(fc: FileChooser): File = fileToReturn
  }

  def runAndWait(block: => Unit): Unit = {
    val latch = new CountDownLatch(1)
    Platform.runLater {
      try { block } finally { latch.countDown() }
    }
    if (!latch.await(5, TimeUnit.SECONDS)) fail("FX Thread timeout")
  }

  "The Gui" should {
    val fileIO = new XmlFileIO()
    val sm = new SessionManager(new SimpleAnalyzer(), new SimpleWebClient(), fileIO)
    
    "initialize and find components" in {
      runAndWait {
        val gui = new TestGui(sm) 
        gui.createScene()
        gui.mainToolbar should not be null
        gui.webView should not be null
      }
    }

    "handle download button click" in {
      runAndWait {
        val gui = new TestGui(sm)
        gui.createScene()
        gui.urlField.text = "button-test"
        
        val btn = gui.mainToolbar.items.collectFirst { 
          case b: javafx.scene.control.Button if b.getId == "dlBtn" => b 
        }.getOrElse(fail("Could not find dlBtn"))
        
        btn.fire()
      }
      Thread.sleep(200)
      sm.data.source should include("button-test")
    }

    "handle File Dialogs" in {
      val temp = File.createTempFile("test", ".xml")
      runAndWait {
        val gui = new TestGui(sm)
        gui.createScene()
        gui.fileToReturn = temp
        
        val btn = gui.mainToolbar.items.collectFirst { 
          case b: javafx.scene.control.Button if b.getId == "openBtn" => b 
        }.get
        btn.fire()
      }
      sm.data.source shouldBe temp.getAbsolutePath
      temp.delete()
    }

    "switch to WebView for HTML" in {
      val htmlData = Data(
        source = "test.html",
        originalLines = List("<html><body>Hi</body></html>"),
        displayLines = List("<html><body>Hi</body></html>"),
        0, 0, Nil, Nil, 0, Nil, Nil
      )
      
      val smHtml = new SessionManager(new SimpleAnalyzer(), new SimpleWebClient(), fileIO) {
        override def data = htmlData
      }

      runAndWait {
        val guiHtml = new TestGui(smHtml)
        guiHtml.createScene()
        guiHtml.update(false)
        
        guiHtml.mainLayout.center.value shouldBe guiHtml.webView.delegate
      }
    }
  }
}