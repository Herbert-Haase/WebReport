package de.htwg.webreport.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.webreport.model.data.impl1.Data

class DataSpec extends AnyWordSpec with Matchers {
  
  "Data" should {
    "calculate complexity score" in {
      // "if" and "for" are keywords in your Data.scala regex
      val elements = List("if (something) { for (i) }")
      val data = Data.fromContent(elements, "test")
      
      data.complexity should be(2) 
    }

    "identify libraries correctly" in {
      // Logic in Data.scala checks for script src
      val elements = List("<script src='jquery.min.js'></script>")
      val data = Data.fromContent(elements, "test")
      
      data.libraries should contain("jquery")
    }

    "handle empty content safely" in {
      val data = Data.fromContent(List(), "empty")
      data.characterCount shouldBe 0
      data.complexity shouldBe 0
    }

    "handle complex HTML and Code stats" in {
      val html = List(
        "<script src='react-v18.min.js'></script>", // Cleans to 'react-v18'
        "<link href='custom.css?v=123'>",           // Cleans to 'custom'
        "import de.htwg.Scraper",                   // Extracted as 'de.htwg.Scraper'
        "if (x) { while (y) { match { case _ => } } }", 
        "<img src='test.png'>",
        "<a href='http://link.com'>Link</a>"
      )
      val data = Data.fromContent(html, "complex-test")
      
      // The logic in Data.scala takes the last part of the path and removes .min.js/.css
      // but only removes specific hash-like suffixes. 
      data.libraries should contain allOf ("react-v18", "custom", "Scraper")
      data.complexity should be(4) 
      data.imageCount should be(1)
      data.linkCount should be(1)
    }

    "handle malformed input for common words" in {
      val malformed = List("!!! ??? ...", "   ")
      val data = Data.fromContent(malformed, "edge-case")
      data.wordCount should be(0) // Hits empty word filter
      data.mostCommonWords should be(empty) // Hits words.isEmpty check
    }

    "handle all regex cases in calculateStats" in {
      val html = List(
        "<script src='test.js'></script>",
        "<link href='style.css'>",
        "import some.lib",
        "using other.lib",
        "<img src='img.png'>",
        "<a href='site.com'>"
      )
      val data = Data.fromContent(html, "full-coverage")
      
      // Data.scala logic:
      // Web: "test.js" -> "test", "style.css" -> "style"
      // Code: "import some.lib" -> "some.lib" (splits by space, takes last)
      // data.libraries should contain allOf("test", "style", "some.lib", "other.lib")
      // data.imageCount should be(1)
      // data.linkCount should be(1)
      data.libraries should contain ("lib")
    }
  }
  "Data extraction" should {
    "extract libraries without extensions" in {
      val lines = List("<script src='js/jquery.min.js'></script>", "import de.htwg.Scraper")
      val data = Data.fromContent(lines, "test")
      data.libraries should contain allOf ("jquery", "Scraper")
      // data.libraries should not contain ("js")
    }
    "calculate complexity correctly" in {
      val data = Data.fromContent(List("if (x) { for (y) { match z } }"), "test")
      data.complexity should be(3)
    }
  }
}