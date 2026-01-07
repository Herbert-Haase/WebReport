package de.htwg.webreport.aview

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.webreport.controller.sessionManager.impl1.SessionManager
import de.htwg.webreport.model.analyzer.impl1.SimpleAnalyzer
import de.htwg.webreport.model.webClient.impl1.SimpleWebClient
import de.htwg.webreport.model.fileio.implXML.XmlFileIO
import java.io.ByteArrayOutputStream
import java.io.ByteArrayInputStream

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
    }

    "handle 'text' command input loop" in {
        val (sm, tui, fileIO) = createDeps()
        val state = new InitialState()
        state.handleInput("invalid", tui, sm, fileIO)
    }
    
    "handle 'import' command" in {
        val (sm, tui, fileIO) = createDeps()
        val state = new InitialState()
        state.handleInput("import ./nonexistent.xml", tui, sm, fileIO)
    }

    "handle 'import' command without arguments" in {
      val (sm, tui, fileIO) = createDeps()
      val state = new InitialState()
      val out = new ByteArrayOutputStream()
      Console.withOut(out) {
        state.handleInput("import", tui, sm, fileIO)
      }
      out.toString should include("Usage: import <filepath>")
    }

    "handle 'text' command with multi-line input" in {
      val (sm, tui, fileIO) = createDeps()
      val state = new InitialState()
      val input = "First line\nSecond line\n.\n"
      val in = new ByteArrayInputStream(input.getBytes)
      
      Console.withIn(in) {
        state.handleInput("text", tui, sm, fileIO)
      }
      
      sm.data.originalLines should contain("First line")
      sm.data.originalLines should contain("Second line")
      tui.state.isInstanceOf[FilterState].shouldBe(true)
    }

    "handle 'exit' and 'quit' commands" in {
      val (sm, tui, fileIO) = createDeps()
      val state = new InitialState()
      
      noException should be thrownBy state.handleInput("exit", tui, sm, fileIO)
      noException should be thrownBy state.handleInput("quit", tui, sm, fileIO)
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
    }
    
    "handle valid filter term" in {
        val (sm, tui, fileIO) = createDeps()
        sm.loadFromText("content")
        val state = new FilterState()
        state.handleInput("content", tui, sm, fileIO)
        sm.data.displayLines should contain("content")
    }
  }
  "FilterState" should {
    "handle 'save' and 'load' with interactive filename input" in {
      val (sm, tui, fileIO) = createDeps()
      sm.loadFromText("some content")
      val state = new FilterState()
      val testFile = "target/test_tui_io.xml"

      val saveInput = new ByteArrayInputStream((testFile + "\n").getBytes)
      Console.withIn(saveInput) {
        state.handleInput("save", tui, sm, fileIO)
      }

      val loadInput = new ByteArrayInputStream((testFile + "\n").getBytes)
      Console.withIn(loadInput) {
        state.handleInput("load", tui, sm, fileIO)
      }
      
      sm.data.originalLines should contain("some content")
    }

    "handle 'exit' command" in {
      val (sm, tui, fileIO) = createDeps()
      val state = new FilterState()
      noException should be thrownBy state.handleInput("exit", tui, sm, fileIO)
    }

    "trigger various commands" in {
        val state = new FilterState()
        val (sm, tui, fileIO) = createDeps()
        state.handleInput("ln", tui, sm, fileIO)
        state.handleInput("scala", tui, sm, fileIO)
        state.handleInput("reset", tui, sm, fileIO)
    }

    "handle commands without crashing" in {
      val state = new FilterState()
      val (sm, tui, fileIO) = createDeps()
      noException should be thrownBy state.handleInput("ln", tui, sm, fileIO)
      
      noException should be thrownBy state.handleInput("someFilterWord", tui, sm, fileIO)
    }
    }
}