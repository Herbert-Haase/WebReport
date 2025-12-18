package de.htwg.webscraper.controller

import de.htwg.webscraper.model.ProjectData
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import scala.util.Success
import java.io.File
import scala.io.Source
import scala.util.Using

class JsonExporterSpec extends AnyWordSpec with Matchers {

  case class MockProjectData(
      originalLines: List[String] = List("line1", "line2"),
      displayLines: List[String] = List("line1"),
      characterCount: Int = 10,
      wordCount: Int = 2,
      mostCommonWords: List[(String, Int)] = List(("line", 1))
  ) extends ProjectData

  "A JsonExporter" should {
    "export ProjectData to a JSON file correctly" in {
      val exporter = new JsonExporter()
      val data = MockProjectData(characterCount = 42, wordCount = 7)
      
      val tempFile = File.createTempFile("test_export", ".json")
      tempFile.deleteOnExit()
      val path = tempFile.getAbsolutePath

      val result = exporter.exportData(data, path)

      result should be(Success(s"Successfully exported to $path"))

      val fileContent = Using(Source.fromFile(tempFile))(_.mkString).get
      
      fileContent should include ("\"stats\": {")
      fileContent should include ("\"characterCount\": 42")
      fileContent should include ("\"wordCount\": 7")
      
      tempFile.delete()
    }

    "return a Failure if the file path is invalid" in {
      val exporter = new JsonExporter()
      val data = MockProjectData()
      
      val result = exporter.exportData(data, "")

      result.isFailure shouldBe true
    }
  }
}
