package de.htwg.webreport.model

import de.htwg.webreport.model.webClient.impl1.SimpleWebClient
import de.htwg.webreport.model.analyzer.impl1.SimpleAnalyzer

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import scala.util.{Success, Failure}
import java.io.{File, PrintWriter}

class ComponentsSpec extends AnyWordSpec with Matchers {

  "A SimpleWebClient" should {
    "retrieve content successfully from a valid URL (Local File)" in {
      val tempFile = File.createTempFile("testContent", ".txt")
      val content = "Hello World"
      new PrintWriter(tempFile) { write(content); close() }

      val url = "file:///" + tempFile.getAbsolutePath.replace("\\", "/")
      val client = new SimpleWebClient

      val result = client.get(url)

      result should be(a [Success[?]])
      result.get should be(content)

      tempFile.delete()
    }

    "return a Failure when given an invalid URL" in {
      val client = new SimpleWebClient
      val result = client.get("invalid-protocol://nothing")

      result should be(a [Failure[?]])
    }
  }

}