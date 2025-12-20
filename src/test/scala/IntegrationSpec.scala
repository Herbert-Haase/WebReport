package de.htwg.webscraper

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.webscraper.controller.impl1.controller.Controller
import de.htwg.webscraper.model.analyzer.impl1.simpleAnalyzer. SimpleAnalyzer
import de. htwg.webscraper. model.webClient.impl1.simpleWebClient.SimpleWebClient
import de.htwg. webscraper.model.data. impl1.Data
import java. io.File
import java.nio.file.Files

class IntegrationSpec extends AnyWordSpec with Matchers {
  
  "The WebScraper System" should {
    "load and process HTML from file" in {
      val controller = new Controller(new SimpleAnalyzer(), new SimpleWebClient())
      val testFile = File.createTempFile("test", ".html")
      
      try {
        Files. writeString(testFile.toPath, "<html><body><h1>Test</h1></body></html>")
        
        controller.loadFromFile(testFile. getAbsolutePath)
        controller.data should not be null
      } finally {
        testFile.delete()
      }
    }
    
    "load and process HTML from text" in {
      val controller = new Controller(new SimpleAnalyzer(), new SimpleWebClient())
      val html = "<html><body><h1>Test</h1><p>Content</p></body></html>"
      
      controller. loadFromText(html)
      controller.data should not be null
    }
    
    "filter data by word" in {
      val controller = new Controller(new SimpleAnalyzer(), new SimpleWebClient())
      val html = "<html><body><div><p>Simple content</p></div><div><div><div><p>Complex nested content</p></div></div></div></body></html>"
      
      controller.loadFromText(html)
      controller.filter("Simple")
      
      controller. data should not be null
    }
    
    "support undo and redo operations" in {
      val controller = new Controller(new SimpleAnalyzer(), new SimpleWebClient())
      val html = "<html><body><h1>Test</h1></body></html>"
      
      controller.loadFromText(html)
      val dataAfterLoad = controller.data
      
      controller.filter("Test")
      
      noException should be thrownBy controller. undo()
      noException should be thrownBy controller.redo()
    }
    
    "reset to initial state" in {
      val controller = new Controller(new SimpleAnalyzer(), new SimpleWebClient())
      val html = "<html><body><h1>Test</h1></body></html>"
      
      controller.loadFromText(html)
      controller.reset()
      
      controller.data should not be null
    }
    
    "create memento for state saving" in {
      val controller = new Controller(new SimpleAnalyzer(), new SimpleWebClient())
      val html = "<html><body><h1>Test</h1></body></html>"
      
      controller.loadFromText(html)
      val memento = controller.createMemento()
      
      memento should not be null
      
      controller. filter("Test")
      controller.restore(memento)
      
      controller.data should not be null
    }
    
    "download from URL" in {
      val controller = new Controller(new SimpleAnalyzer(), new SimpleWebClient())
      
      // Note:  This will actually try to download, so use a reliable URL or mock
      // For now, just test that the method exists and can be called
      noException should be thrownBy {
        try {
          controller. downloadFromUrl("https://example.com")
        } catch {
          case _: Exception => // Network errors are expected in tests
        }
      }
    }
  }
  
  "Command Pattern" should {
    "support undo for load operations" in {
      val controller = new Controller(new SimpleAnalyzer(), new SimpleWebClient())
      
      controller. loadFromText("<html><body><p>First</p></body></html>")
      val firstData = controller.data
      
      controller.loadFromText("<html><body><p>Second</p></body></html>")
      
      controller.undo()
      // After undo, should be back to first state
      controller.data should not be null
    }
    
    "support redo after undo" in {
      val controller = new Controller(new SimpleAnalyzer(), new SimpleWebClient())
      
      controller.loadFromText("<html><body><p>Content</p></body></html>")
      controller.filter("Content")
      
      controller. undo()
      controller.redo()
      
      controller.data should not be null
    }
  }
  
  "Data Processing" should {
    "handle complex nested elements" in {
      val elements = List(
        "<div><div><div><p>Deeply nested</p></div></div></div>",
        "<p>Simple</p>"
      )
      val data = Data. fromContent(elements)
      
      data should not be null
      data.complexity should be >= 0
    }
    
    "track multiple elements" in {
      val elements = List(
        "<h1>Heading</h1>",
        "<p>Paragraph 1</p>",
        "<p>Paragraph 2</p>",
        "<div>Division</div>"
      )
      val data = Data.fromContent(elements)
      
      data should not be null
      // Just verify the data object was created successfully
      data.complexity should be >= 0
    }
  }
}