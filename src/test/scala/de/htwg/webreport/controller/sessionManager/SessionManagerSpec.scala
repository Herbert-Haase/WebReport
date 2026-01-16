package de.htwg.webreport.controller.sessionManager

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.webreport.model.webClient.WebClientTrait
import de.htwg.webreport.model.webClient.impl1.SimpleWebClient
import de.htwg.webreport.model.analyzer.impl1.SimpleAnalyzer
import de.htwg.webreport.controller.sessionManager.impl1.SessionManager
import de.htwg.webreport.model.fileio.implXML.XmlFileIO
import scala.util.Failure
import scala.util.Try

class sessionManagerSpec extends AnyWordSpec with Matchers {

  class FailingClient extends WebClientTrait {
  override def get(url: String): Try[String] = 
    Failure(new RuntimeException("Injected Network Error"))

  override def download(url: String): Try[String] = 
    Failure(new RuntimeException("Injected Network Error"))
  }
  
  def createsessionManager(): SessionManager = {
    val client = new SimpleWebClient()
    val analyzer = new SimpleAnalyzer()
    val fileIO = new XmlFileIO()
    new SessionManager(analyzer, client, fileIO)
  }

  "A sessionManager" should {
    "load text correctly via Command" in {
      val sessionManager = createsessionManager()
      sessionManager.loadFromText("Hello\nWorld")
      sessionManager.data.displayLines should be(List("Hello", "World"))
    }

    "filter text correctly" in {
      val sessionManager = createsessionManager()
      sessionManager.loadFromText("Apple\nBanana\nApricot")
      sessionManager.filter("Ap")
      sessionManager.data.displayLines should contain("Apple")
      sessionManager.data.displayLines should not contain("Banana")
    }

    "Undo and Redo correctly" in {
      val sessionManager = createsessionManager()
      sessionManager.loadFromText("Initial")
      
      sessionManager.filter("NonExistentSearchTerm")
      sessionManager.data.displayLines should be(empty)
      
      sessionManager.undo()
      sessionManager.data.displayLines should be(List("Initial"))
      
      sessionManager.redo()
      sessionManager.data.displayLines should be(empty)
    }

    "handle download failure gracefully" in {
      val sessionManager = createsessionManager()
      sessionManager.downloadFromUrl("http://invalid.url.local")
      
      sessionManager.data.displayLines.head should startWith("Error")
    }

    "handle download failure with specific message" in {
      val sessionManager = createsessionManager()
      sessionManager.downloadFromUrl("http://non-existent-domain-12345.com")
      
      sessionManager.data.displayLines.mkString should include("Error")
      sessionManager.data.source should be("http://non-existent-domain-12345.com")
    }

    "handle failed downloads by creating error data" in {
      val sm = createsessionManager()
      sm.downloadFromUrl("not-a-valid-url")
      
      sm.data.source should include ("not-a-valid-url")
      sm.data.originalLines.head should startWith ("Error")
    }
    "handle download failure gracefully with real client" in {
      val sessionManager = createsessionManager()
      sessionManager.downloadFromUrl("http://invalid.url.local")
      sessionManager.data.displayLines.head should startWith("Error")
    }
    "cover the Failure branch using a stubbed client" in {
    val failingClient = new FailingClient()
    val sessionManager = new SessionManager(new SimpleAnalyzer(), failingClient, new XmlFileIO())
    
    sessionManager.downloadFromUrl("http://any-url.com")
    
    sessionManager.data.displayLines.head should include("Error")
    sessionManager.data.displayLines.head should include("Injected Network Error")
  }


    "Undo and Redo SetStateCommand via loadFromFile success" in {
    val mockFileIO = new de.htwg.webreport.model.fileio.FileIOTrait {
      override val mode: String = "MOCK"
      override def save(data: List[de.htwg.webreport.model.data.DataTrait], path: String): Unit = {}
      override def load(path: String): List[de.htwg.webreport.model.data.DataTrait] = {
        val analyzer = new de.htwg.webreport.model.analyzer.impl1.SimpleAnalyzer()
        List(analyzer.process(List("Historical State"), None, "history-src"))
      }
    }
    val sm = new de.htwg.webreport.controller.sessionManager.impl1.SessionManager(
      new de.htwg.webreport.model.analyzer.impl1.SimpleAnalyzer(),
      new de.htwg.webreport.model.webClient.impl1.SimpleWebClient(),
      mockFileIO
    )

    sm.loadFromFile("dummy-path")
    sm.data.displayLines should be(List("Historical State"))

    sm.undo()
    sm.data.displayLines should be(empty)

    sm.redo()
    sm.data.displayLines should be(List("Historical State"))
  }

    "Undo and Redo LoadCommand via loadFromText" in {
    val sessionManager = createsessionManager()
    
    sessionManager.loadFromText("Text for LoadCommand") 
    sessionManager.data.displayLines should be(List("Text for LoadCommand"))
    
    sessionManager.undo()
    sessionManager.data.displayLines should be(empty)
    
    sessionManager.redo()
    sessionManager.data.displayLines should be(List("Text for LoadCommand"))
  }


    "Undo and Redo DownloadCommand correctly" in {
    val sessionManager = createsessionManager()
    
    sessionManager.downloadFromUrl("http://undo-test-url.com")
    val downloadedState = sessionManager.data
    downloadedState.source should be("http://undo-test-url.com")
    
    sessionManager.undo()
    sessionManager.data.source should be("empty")
    
    sessionManager.redo()
    sessionManager.data.source should be("http://undo-test-url.com")
    sessionManager.data.displayLines should be (downloadedState.displayLines)
  }

    "Undo and redo DownloadCommand correctly" in {
    val failingClient = new FailingClient()
    val sm = new de.htwg.webreport.controller.sessionManager.impl1.SessionManager(
      new de.htwg.webreport.model.analyzer.impl1.SimpleAnalyzer(), 
      failingClient, 
      new de.htwg.webreport.model.fileio.implXML.XmlFileIO()
    )
    
    sm.downloadFromUrl("http://test-undo-redo.com")
    sm.data.source should be("http://test-undo-redo.com")
    
    sm.undo()
    sm.data.source should be("empty")
    
    sm.redo()
    sm.data.source should be("http://test-undo-redo.com")
  }

  }
}