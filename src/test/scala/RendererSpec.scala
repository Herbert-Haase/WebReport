package de.htwg.webscraper.aview

import de.htwg.webscraper.model.Data
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class RendererSpec extends AnyWordSpec with Matchers {
  
  val sampleData = Data(List("Hello World", "Scala is Fun"))
  val width = 20

  "A SimpleReport" should {
    "render text within borders" in {
      val renderer = new SimpleReport()
      val output = renderer.render(sampleData, width)
      
      output should include(s"+${"-" * width}+") 
      output should include("|Hello World         |") 
      output should include("|Scala is Fun        |")
    }

    "wrap lines that are too long" in {
      val longData = Data(List("ThisLineIsLongerThanTwentyCharacters"))
      val renderer = new SimpleReport()
      val output = renderer.render(longData, 20)
      
      // Should split into chunks of 20
      output should include("|ThisLineIsLongerThan|")
      output should include("|TwentyCharacters    |")
    }
  }

  "A LowerCaseDecorator" should {
    "convert content to lowercase" in {
      val renderer = new LowerCaseDecorator(new SimpleReport())
      val output = renderer.render(sampleData, width)
      
      output should include("|hello world         |")
      output should include("|scala is fun        |")
    }

    "NOT convert headers or stats to lowercase" in {
      val renderer = new LowerCaseDecorator(new SimpleReport())
      val output = renderer.render(sampleData, width)
      
      output should include("[Stats]") // Validates that stats header is intact
    }
  }

  "Stacked Decorators (Numbers + LowerCase)" should {
    "work together regardless of nesting order in logic" in {
      // Logic from Tui: Numbers(LowerCase(Simple))
      val renderer = new LineNumberDecorator(new LowerCaseDecorator(new SimpleReport()))
      val output = renderer.render(sampleData, width)
      
      // 1. |hello world... |
      output should include("1. |hello world         |")
      output should include("2. |scala is fun        |")
    }
  }
}