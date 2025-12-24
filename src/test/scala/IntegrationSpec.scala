package de.htwg.webreport

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.webreport.controller.sessionManager.impl1.SessionManager
import de.htwg.webreport.model.analyzer.impl1.SimpleAnalyzer
import de.htwg.webreport.model.webClient.impl1.SimpleWebClient
import de.htwg.webreport.model.fileio.implXML.XmlFileIO
import de.htwg.webreport.aview.Tui
import java.io.File
import java.nio.file.Files

class IntegrationSpec extends AnyWordSpec with Matchers {

  def createSystem() = {
    val fileIO = new XmlFileIO()
    val sessionManager = new SessionManager(new SimpleAnalyzer(), new SimpleWebClient(), fileIO)
    val tui = new Tui(sessionManager, fileIO)
    (sessionManager, tui, fileIO)
  }

  "The WebScraper System" should {
    "transition from InitialState to FilterState when data is loaded via text" in {
      val (sessionManager, tui, _) = createSystem()
      
      sessionManager.data.source should be("empty")
      sessionManager.loadFromText("Line 1\nLine 2")
      
      sessionManager.data.source should be("text-input") 
      sessionManager.data.displayLines should have size 2
    }

    "successfully save and load a session via FileIO" in {
      val (sessionManager, _, fileIO) = createSystem()
      val tempFile = File.createTempFile("session", ".xml")
      
      try {
        sessionManager.loadFromText("Test Content")
        sessionManager.saveSession(tempFile.getAbsolutePath)
        
        // Reset and Load
        sessionManager.reset()
        sessionManager.data.displayLines should be(empty)
        
        sessionManager.loadFromFile(tempFile.getAbsolutePath)
        sessionManager.data.displayLines should contain("Test Content")
      } finally {
        tempFile.delete()
      }
    }

    "maintain complexity stats across filtering" in {
      val (sessionManager, _, _) = createSystem()
      // "if" is a complexity keyword
      sessionManager.loadFromText("if (condition) {\n  doSomething()\n}")
      val initialComplexity = sessionManager.data.complexity
      
      sessionManager.filter("condition")
      sessionManager.data.complexity should be(initialComplexity)
    }

    "handle all TUI commands in FilterState" in {
      val (sessionManager, tui, _) = createSystem()
      sessionManager.loadFromText("content")
      val filterState = new de.htwg.webreport.aview.FilterState()
      
      noException should be thrownBy filterState.handleInput("ln", tui, sessionManager, null)
      noException should be thrownBy filterState.handleInput("undo", tui, sessionManager, null)
      noException should be thrownBy filterState.handleInput("redo", tui, sessionManager, null)
      
      filterState.handleInput("reset", tui, sessionManager, null)
      sessionManager.data.displayLines should be(empty)
    }
  }
}