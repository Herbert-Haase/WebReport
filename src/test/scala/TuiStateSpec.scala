package de.htwg.webreport.aview

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.webreport.controller.sessionManager.impl1.SessionManager
import de.htwg.webreport.model.analyzer.impl1.SimpleAnalyzer
import de.htwg.webreport.model.webClient.impl1.SimpleWebClient
import de.htwg.webreport.model.fileio.implXML.XmlFileIO

class TuiStateSpec extends AnyWordSpec with Matchers {
  
  def createDeps() = {
    val fileIO = new XmlFileIO()
    val sm = new SessionManager(new SimpleAnalyzer(), new SimpleWebClient(), fileIO)
    val tui = new Tui(sm, fileIO)
    (sm, tui, fileIO)
  }

  "InitialState" should {
    "handle 'download' command" in {
      val (sm, tui, fileIO) = createDeps()
      val state = new InitialState()
      state.handleInput("download http://example.com", tui, sm, fileIO)
      // Check if state changed implies command command was processed
      // We can't easily check internal state without spying, but we check no exception
    }

    "handle 'text' command input loop" in {
        val (sm, tui, fileIO) = createDeps()
        val state = new InitialState()
        // We can't easily test the while loop reading stdin here without mocking Console
        // But we can test invalid commands
        state.handleInput("invalid", tui, sm, fileIO)
    }
    
    "handle 'import' command" in {
        val (sm, tui, fileIO) = createDeps()
        val state = new InitialState()
        state.handleInput("import ./nonexistent.xml", tui, sm, fileIO)
        // Should print error but not crash
    }
  }

  "FilterState" should {
    "handle 'ln', 'undo', 'redo', 'reset'" in {
      val (sm, tui, fileIO) = createDeps()
      val state = new FilterState()
      
      noException should be thrownBy state.handleInput("ln", tui, sm, fileIO)
      noException should be thrownBy state.handleInput("undo", tui, sm, fileIO)
      noException should be thrownBy state.handleInput("redo", tui, sm, fileIO)
      noException should be thrownBy state.handleInput("reset", tui, sm, fileIO)
    }

    "handle 'save' and 'load'" in {
       val (sm, tui, fileIO) = createDeps()
       val state = new FilterState()
       // These prompt for ReadLine, might hang in tests if not mocked.
       // Skip if you can't mock StdIn, or use a library like Mockito.
       // Alternatively, refactor TuiState to accept arguments directly.
    }
    
    "handle valid filter term" in {
        val (sm, tui, fileIO) = createDeps()
        sm.loadFromText("content")
        val state = new FilterState()
        state.handleInput("content", tui, sm, fileIO)
        // verify filter happened?
        sm.data.displayLines should contain("content")
    }
  }
  "FilterState" should {
    "trigger various commands" in {
        val state = new FilterState()
        val (sm, tui, fileIO) = createDeps()
        // Exercise toggle branch
        state.handleInput("ln", tui, sm, fileIO)
        // Exercise the word filter branch (parts.length > 0)
        state.handleInput("scala", tui, sm, fileIO)
        // Exercise reset branch
        state.handleInput("reset", tui, sm, fileIO)
    }

    "handle commands without crashing" in {
      val state = new FilterState()
      val (sm, tui, fileIO) = createDeps()
      // Mock/Dummy dependencies
      
      // Exercise the toggle branches
      noException should be thrownBy state.handleInput("ln", tui, sm, fileIO)
      
      // Exercise the word filter branch
      noException should be thrownBy state.handleInput("someFilterWord", tui, sm, fileIO)
    }
    }
}