package de.htwg.webreport

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.webreport.aview.Tui
import de.htwg.webreport.controller.sessionManager.impl1.SessionManager
import de.htwg.webreport.model.analyzer.impl1.SimpleAnalyzer
import de.htwg.webreport.model.webClient.impl1.SimpleWebClient
import de.htwg.webreport.model.fileio.implXML.XmlFileIO 
import de.htwg.webreport.aview.FilterState

class TuiSpec extends AnyWordSpec with Matchers {
  
  "The Tui" should {
    "toggle line numbers" in {
      val sessionManager = new SessionManager(new SimpleAnalyzer(), new SimpleWebClient(), new XmlFileIO())
      val tui = new Tui(sessionManager, new XmlFileIO())
      
      noException should be thrownBy tui.toggleLineNumbers()
    }
    
    "toggle lowercase" in {
      val sessionManager = new SessionManager(new SimpleAnalyzer(), new SimpleWebClient(), new XmlFileIO())
      val tui = new Tui(sessionManager, new XmlFileIO())
      
      noException should be thrownBy tui.toggleLowerCase()
    }
    
    "update without error" in {
      val sessionManager = new SessionManager(new SimpleAnalyzer(), new SimpleWebClient(), new XmlFileIO())
      val tui = new Tui(sessionManager, new XmlFileIO())
      
      noException should be thrownBy tui.update(false)
    }
  }
    "The Tui States" should {
      "handle FilterState commands" in {
        val sm = new SessionManager(new SimpleAnalyzer(), new SimpleWebClient(), new XmlFileIO())
        val tui = new Tui(sm, new XmlFileIO())
        val state = new FilterState()
        
        noException should be thrownBy state.handleInput("undo", tui, sm, sm.fileIO)
        noException should be thrownBy state.handleInput("ln", tui, sm, sm.fileIO)
        noException should be thrownBy state.handleInput("invalid_cmd", tui, sm, sm.fileIO)
      }
    }
}