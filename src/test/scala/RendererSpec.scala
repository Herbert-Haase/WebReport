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
    "The Renderer (Edge Cases)" should {
    
    // Covers wrapLines: {48 wrappedLines += ""}
    "preserve empty lines in wrapLines" in {
      val data = Data(List("Line 1", "", "Line 3"))
      val renderer = new SimpleReport()
      val output = renderer.render(data, 20)
      
      output should include("|Line 1              |")
      output should include("|                    |") // This implies the empty line was handled
      output should include("|Line 3              |")
    }

    // Covers wrapLines: {56-58} where a huge word forces a split of the *previous* content
    "wrap correctly when a huge word follows a normal word" in {
      // Width 5. "Hi" (2) fits. "WorldLong" (9) is huge.
      // Logic should: flush "Hi", then chunk "WorldLong"
      val data = Data(List("Hi WorldLong")) 
      val renderer = new SimpleReport()
      val output = renderer.render(data, 5)

      // Expected:
      // |Hi   | (Flushed previous content)
      // |World| (Chunk 1)
      // |Long | (Chunk 2)
      output should include("|Hi   |")
      output should include("|World|")
      output should include("|Long |")
    }

    // Covers wrapLines: {62-65} standard wrapping where word doesn't fit on current line
    "wrap standard words to next line" in {
      // Width 10. "Hello" (5) + " " (1) + "World" (5) = 11 > 10.
      // "World" must wrap.
      val data = Data(List("Hello World"))
      val renderer = new SimpleReport()
      val output = renderer.render(data, 10)

      output should include("|Hello     |")
      output should include("|World     |")
    }
  }
    "The RendererDecorator" should {
    // Covers RendererDecorator abstract class delegation {81-83}
    "delegate render calls correctly in the abstract class" in {
      val data = Data(List("Test"))
      val mockRenderer = new Renderer {
        override def render(d: Data, w: Int): String = "Mocked"
      }
      
      // Anonymous class extending abstract decorator to test the delegation line explicitly
      val decorator = new RendererDecorator(mockRenderer) {}
      
      decorator.render(data, 10) should be("Mocked")
    }
  }
}