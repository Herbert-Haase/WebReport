package de.htwg.webscraper

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest. matchers.should.Matchers
import de.htwg.webscraper.aview.Tui
import de.htwg.webscraper.controller.impl1.controller.Controller
import de.htwg.webscraper.model. data.impl1.Data
import de.htwg.webscraper.model.analyzer.impl1.simpleAnalyzer.SimpleAnalyzer
import de.htwg.webscraper.model.webClient.impl1.simpleWebClient.SimpleWebClient
import de.htwg.webscraper.controller.exporter.impl1.jsonExporter.JsonExporter

class TuiSpec extends AnyWordSpec with Matchers {
  
  "The Tui" should {
    "toggle line numbers" in {
      val controller = new Controller(new SimpleAnalyzer(), new SimpleWebClient())
      val exporter = new JsonExporter()
      val tui = new Tui(controller, exporter)
      
      noException should be thrownBy tui.toggleLineNumbers()
    }
    
    "toggle lowercase" in {
      val controller = new Controller(new SimpleAnalyzer(), new SimpleWebClient())
      val exporter = new JsonExporter()
      val tui = new Tui(controller, exporter)
      
      noException should be thrownBy tui.toggleLowerCase()
    }
    
    "update and display information" in {
      val controller = new Controller(new SimpleAnalyzer(), new SimpleWebClient())
      val exporter = new JsonExporter()
      val tui = new Tui(controller, exporter)
      
      noException should be thrownBy tui.update()
    }
  }
}

class DataSpec extends AnyWordSpec with Matchers {
  
  "Data" should {
    "calculate image count from HTML elements" in {
      val elements = List(
        "<img src='test1.jpg'>",
        "<img src='test2.jpg'>",
        "<p>Some text</p>"
      )
      val data = Data.fromContent(elements)
      
      data.imageCount shouldBe 2
    }
    
    "calculate link count from HTML elements" in {
      val elements = List(
        "<a href='link1'>Link 1</a>",
        "<a href='link2'>Link 2</a>",
        "<p>Some text</p>"
      )
      val data = Data. fromContent(elements)
      
      data.linkCount shouldBe 2
    }
    
    "handle empty content" in {
      val data = Data.fromContent(List())
      
      data.imageCount shouldBe 0
      data.linkCount shouldBe 0
    }
    
    "create from filtered data" in {
      val elements = List(
        "<div>Content 1</div>",
        "<div>Content 2</div>"
      )
      val filteredElements = List("<div>Content 1</div>")
      val originalData = Data.fromContent(elements)
      val filteredData = Data.fromFiltered(filteredElements, elements)
      
      filteredData should not be null
    }
    
    "handle apply method" in {
      val elements = List("<p>Test</p>")
      val data = Data.apply(elements)
      
      data should not be null
    }
  }
}

class JsonExporterSpec extends AnyWordSpec with Matchers {
  import de.htwg.webscraper.controller.exporter.impl1.jsonExporter.JsonExporter
  import java.io.File
  
  "JsonExporter" should {
    "export data to JSON file" in {
      val elements = List("<html><body><h1>Test</h1></body></html>")
      val data = Data. fromContent(elements)
      val exporter = new JsonExporter()
      val testFile = new File("test_export.json")
      
      try {
        exporter.exportData(data, testFile. getAbsolutePath)
        testFile.exists() shouldBe true
      } finally {
        if (testFile.exists()) testFile.delete()
      }
    }
  }
}

class XmlExporterSpec extends AnyWordSpec with Matchers {
  import de.htwg. webscraper.controller.exporter.impl2.XmlExporter
  import java. io.File
  
  "XmlExporter" should {
    "export data to XML file" in {
      val elements = List("<html><body><h1>Test</h1></body></html>")
      val data = Data.fromContent(elements)
      val exporter = new XmlExporter()
      val testFile = new File("test_export.xml")
      
      try {
        exporter.exportData(data, testFile.getAbsolutePath)
        testFile.exists() shouldBe true
      } finally {
        if (testFile.exists()) testFile.delete()
      }
    }
  }
}

class MainSpec extends AnyWordSpec with Matchers {
  
  "Main" should {
    "have a defined object" in {
      noException should be thrownBy {
        de.htwg.webscraper.Main
      }
    }
  }
}

class WebScraperModuleSpec extends AnyWordSpec with Matchers {
  
  "WebScraperModule" should {
    "be instantiable" in {
      noException should be thrownBy {
        new de.htwg.webscraper.WebScraperModule()
      }
    }
  }
}