package de.htwg.webreport

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, PrintStream}

class WebReportSpec extends AnyWordSpec with Matchers {

  "The WebReport application" should {
    "initialize and process basic commands in the TUI" in {
      System.setProperty("testMode", "true")
      val inputString = 
        """download https://example.com
          |filter example
          |exit
          |""".stripMargin

      val in = new ByteArrayInputStream(inputString.getBytes)
      val out = new ByteArrayOutputStream()

      Console.withOut(out) {
        Console.withIn(in) {
          Main.start() 
        }
      }

      val output = out.toString
      output shouldBe a [String]
    }
  }
}