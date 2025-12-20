package de.htwg.webscraper.model

import de.htwg.webscraper.model.webClient.impl1.simpleWebClient.*
import de.htwg.webscraper.model.analyzer.impl1.simpleAnalyzer.*

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import scala.util.{Success, Failure}
import java.io.{File, PrintWriter}

class ComponentsSpec extends AnyWordSpec with Matchers {

  "A SimpleWebClient" should {
    "retrieve content successfully from a valid URL (Local File)" in {
      // Create a temporary file to simulate a valid URL target
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

  "A SimpleAnalyzer" should {
    "analyze the filtered list when it is not empty (Logic Branch)" in {
      val analyzer = new SimpleAnalyzer
      val originalLines = List("Original line A", "Original line B")
      val filteredLines = List("Filtered text here")

      val data = analyzer.process(originalLines, filteredLines)

      data.characterCount should be(18)
      data.wordCount should be(3)

      data.originalLines should be(originalLines)
      data.displayLines should be(filteredLines)
    }
  }
}