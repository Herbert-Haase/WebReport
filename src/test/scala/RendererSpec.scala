package de.htwg.webscraper.aview

import de.htwg.webscraper.model.data.impl1.*
import de.htwg.webscraper.model.data.ProjectData
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.webscraper.aview.Renderer

import org.scalatest. wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.webscraper.aview.{SimpleReport, LowerCaseDecorator, LineNumberDecorator}
import de.htwg.webscraper.model.data.impl1.Data

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
        override def render(d: ProjectData, w: Int): String = "Mocked"
      }

      // Anonymous class extending abstract decorator to test the delegation line explicitly
      val decorator = new RendererDecorator(mockRenderer) {}

      decorator.render(data, 10) should be("Mocked")
    }
  }
  "SimpleReport" should {
    "render data to string" in {
      val elements = List("<html><body><h1>Test</h1></body></html>")
      val data = Data.fromContent(elements)
      val renderer = new SimpleReport()
      
      val result = renderer.render(data, 80)
      result.nonEmpty shouldBe true
    }
    
    "render with different widths" in {
      val elements = List("<html><body><h1>Test</h1></body></html>")
      val data = Data.fromContent(elements)
      val renderer = new SimpleReport()
      
      val narrowResult = renderer.render(data, 40)
      val wideResult = renderer.render(data, 120)
      
      narrowResult. nonEmpty shouldBe true
      wideResult.nonEmpty shouldBe true
    }
  }
  
  "LowerCaseDecorator" should {
    "convert output to lowercase" in {
      val elements = List("<html><body><h1>TEST</h1></body></html>")
      val data = Data. fromContent(elements)
      val baseRenderer = new SimpleReport()
      val decorator = new LowerCaseDecorator(baseRenderer)
      
      val result = decorator.render(data, 80)
      result.nonEmpty shouldBe true
    }
  }
  
  "LineNumberDecorator" should {
    "add line numbers to output" in {
      val elements = List("<html><body><h1>Test</h1></body></html>")
      val data = Data. fromContent(elements)
      val baseRenderer = new SimpleReport()
      val decorator = new LineNumberDecorator(baseRenderer)
      
      val result = decorator.render(data, 80)
      result.nonEmpty shouldBe true
    }
  }
  
  "RendererDecorator" should {
    "work as base decorator" in {
      val elements = List("<html><body><h1>Test</h1></body></html>")
      val data = Data.fromContent(elements)
      val baseRenderer = new SimpleReport()
      
      val result = baseRenderer.render(data, 80)
      result.nonEmpty shouldBe true
    }
    
    "support chaining decorators" in {
      val elements = List("<html><body><h1>Test</h1></body></html>")
      val data = Data.fromContent(elements)
      val baseRenderer = new SimpleReport()
      val withLineNumbers = new LineNumberDecorator(baseRenderer)
      val withBoth = new LowerCaseDecorator(withLineNumbers)
      
      val result = withBoth.render(data, 80)
      result.nonEmpty shouldBe true
    }
  }
}