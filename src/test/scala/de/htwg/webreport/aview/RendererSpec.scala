package de.htwg.webreport.aview

import de.htwg.webreport.model.data.impl1.Data
import de.htwg.webreport.model.data.DataTrait
import de.htwg.webreport.aview.Renderer
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import scala.io.AnsiColor

class RendererSpec extends AnyWordSpec with Matchers {

  val sampleData = Data(List("Hello World", "Scala is Fun"), "test-source")
  val width = 40

  "A SimpleReport" should {
    "render text within Unicode borders" in {
      val renderer = new SimpleReport()
      val output = renderer.render(sampleData, width)

      output should include("┌" + "─" * (width - 2) + "┐")
      output should include("└" + "─" * (width - 2) + "┘")
      
      output should include("│ Hello World")
      output should include("│ Scala is Fun")
    }

    "render the Dashboard when data is present" in {
      val renderer = new SimpleReport()
      val output = renderer.render(sampleData, width)
      
      output should include("DASHBOARD")
      output should include("CHARS:")
      output should include("COMPLEXITY:")
      output should include("LIBS :")
    }

    "render the Welcome Screen when data source is 'empty'" in {
      val emptyData = Data.fromContent(List(), "empty") 
      val renderer = new SimpleReport()
      val output = renderer.render(emptyData, width)

      output should include("text")
      output should include("Use the GUI")
      output should not include("DASHBOARD")
    }

    "wrap lines that are too long" in {
      val longLine = "ThisLineIsLongerThanTwentyCharactersAndNeedsWrapping"
      val longData = Data.fromContent(List(longLine), "test")
      val renderer = new SimpleReport()
      
      val output = renderer.render(longData, 21)

      output should include("ThisLineIsLongerT")
      output should include("hanTwentyCharacte")
      output should include("rsAndNeedsWrappin")
      }

    "stack multiple decorators" in {
      val data = Data.fromContent(List("First", "Second"), "test")
      val renderer = new LineNumberDecorator(new SimpleReport())
      val output = renderer.render(data, 40)
      
      output should include(" 1. │ First") 
    }

    "render different complexity colors" in {
      val renderer = new SimpleReport()
      
      val yellowData = Data(List("for\n"*50, "B\n" * 1000), "source")
      renderer.render(yellowData, 80) should include(AnsiColor.YELLOW)

      val redData = Data(List("for\n"*500, "B\n" * 1000), "source")
      renderer.render(redData, 80) should include(AnsiColor.RED)
    }
    "display famous libraries using mkString" in {
      val renderer = new SimpleReport()
      val baseData = Data.fromContent(List("some content"), "source")
      val dataWithLibs = baseData.copy(
        libraries = List("react-js", "jquery-min")
      )
      
      val output = renderer.render(dataWithLibs, 80)
      
      output should include("react-js, jquery-min")
    }


    "trigger the early return for welcome screen" in {
      val renderer = new SimpleReport()
      val emptyData = Data.fromContent(Nil, "empty")
      
      val output = renderer.render(emptyData, 80)
      output should include("██╗")
    }

    "explicitly trigger the early return on line 109" in {
      val renderer = new SimpleReport()
      
      val emptyData = de.htwg.webreport.model.data.impl1.Data(
        source = "empty",
        originalLines = Nil,
        displayLines = Nil,
        characterCount = 0,
        wordCount = 0,
        mostCommonWords = Nil,
        libraries = Nil,
        complexity = 0,
        images = Nil,
        links = Nil
      )

      val output = renderer.render(emptyData, 80)

      output should include("██╗")
      output should not include("DASHBOARD")
    }
  }
  "A SimpleReport" should {
    "handle very narrow widths by defaulting to 80" in {
      val renderer = new SimpleReport()
      val output = renderer.render(sampleData, 10)
      output should include("─" * 78) 
    }
    
    "work correctly with the LineNumberDecorator" in {
      val decorated = new LineNumberDecorator(new SimpleReport())
      val output = decorated.render(sampleData, 40)
      output should include(" 1. │")
    }
  }
  "ReportRenderer" should {
    "cover the Decorator path" in {
      val data = Data.fromContent(List("test"), "src")
      val decorator = new SimpleReport()
      decorator.render(data,10).should(include("test"))
    }

    "cover all branches in ReportTemplate" in {
      val template = new SimpleReport()
      val fullReport = template.render(Data.fromContent(List("val x = 1"), "src"), 80)
      fullReport.should(include("COMPLEXITY"))
      val emptyReport = template.render(Data.fromContent(Nil, "empty"), 80)
      emptyReport.should(not(be(empty))) 
    }
  }

  "RendererDecorator" should {
    "execute the base render method" in {
      val inner = new SimpleReport()
      val baseDecorator = new RendererDecorator(inner) {}
      
      val data = Data.fromContent(List("test"), "src")
      val output = baseDecorator.render(data, 80)
      
      output shouldBe inner.render(data, 80)
    }
  }
}