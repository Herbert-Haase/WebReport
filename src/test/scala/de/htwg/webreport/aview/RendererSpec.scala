package de.htwg.webreport.aview

import de.htwg.webreport.model.data.impl1.Data
import de.htwg.webreport.model.data.DataTrait
import de.htwg.webreport.aview.Renderer
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

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
}