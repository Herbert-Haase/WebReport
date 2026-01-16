package de.htwg.webreport.model.fileio.implJSON

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.webreport.model.data.impl1.Data
import java.io.File
import play.api.libs.json.Json

class JsonFileIOSpec extends AnyWordSpec with Matchers {
  "JsonFileIO" should {
    val fileIO = new JsonFileIO()
    
    "have the correct mode" in {
      fileIO.mode should be("JSON")
    }

    "JsonFileIO" should {
      "save and load session data correctly" in {
        val fileIO = new de.htwg.webreport.model.fileio.implJSON.JsonFileIO()
        val tempFile = java.io.File.createTempFile("session", ".json")
        val sample = List(Data.fromContent(List("Line 1"), "json-test"))
        
        try {
          fileIO.save(sample, tempFile.getAbsolutePath)
          val loaded = fileIO.load(tempFile.getAbsolutePath)
          
          loaded.head.source should be("json-test")
          loaded.head.displayLines should contain("Line 1")
        } finally {
          tempFile.delete()
        }
      }

      "use the implicit dataFormat" in {
        val fileIO = new JsonFileIO()
        import fileIO.dataFormat
        val data = Data.fromContent(List("test"), "src")
        val json = play.api.libs.json.Json.toJson(data)
        json.as[Data].source shouldBe "src"
      }

      "handle non-concrete DataTrait implementations (the 'other' branch)" in {
        val fileIO = new JsonFileIO()
        val tempFile = java.io.File.createTempFile("other_data", ".json")
        
        val otherData = new de.htwg.webreport.model.data.DataTrait {
          def source = "custom"
          def originalLines = List("line1")
          def displayLines = List("line1")
          def characterCount = 5
          def wordCount = 1
          def mostCommonWords = Nil
          def libraries = List("custom-lib")
          def complexity = 0
          def images = List("img.png")
          def links = List("link.com")
          def imageCount = 1
          def linkCount = 1
        }

        try {
          fileIO.save(List(otherData), tempFile.getAbsolutePath)
          val loaded = fileIO.load(tempFile.getAbsolutePath)
          loaded.head.source shouldBe "custom"
        } finally { tempFile.delete() }
      }
    }
    "save and load a list of Data objects correctly" in {
        val tempFile = File.createTempFile("test_session", ".json")
        val data1 = Data.fromContent(List("Line 1"), "source1")
        val data2 = Data.fromContent(List("Line 2"), "source2")
        val list = List(data1, data2)

        try {
          fileIO.save(list, tempFile.getAbsolutePath)
          val loaded = fileIO.load(tempFile.getAbsolutePath)

          loaded should have size 2
          loaded.head.source should be("source1")
          loaded.last.displayLines should contain("Line 2")
        } finally {
          tempFile.delete()
        }
      }

    "handle loading from a non-existent file gracefully (or fail depending on implementation)" in {
       val tempFile = File.createTempFile("empty", ".json")
       tempFile.delete()
       
       assertThrows[Exception] {
         fileIO.load(tempFile.getAbsolutePath)
       }
    }
    
    "handle loading malformed JSON" in {
        val tempFile = File.createTempFile("bad", ".json")
        val pw = new java.io.PrintWriter(tempFile)
        pw.write("{ bad json }")
        pw.close()
        
        assertThrows[com.fasterxml.jackson.core.JsonParseException] {
          fileIO.load(tempFile.getAbsolutePath)
        }
        tempFile.delete()
    }

    "explicitly exercise the implicit dataFormat for JSON serialization" in {
    val fileIO = new JsonFileIO()
    import fileIO.dataFormat
    
    val sampleData = Data(
      source = "test-format",
      originalLines = List("line1"),
      displayLines = List("line1"),
      characterCount = 5,
      wordCount = 1,
      mostCommonWords = List(("line1", 1)),
      libraries = List("scala"),
      complexity = 10,
      images = Nil,
      links = Nil
    )

    val json = Json.toJson(sampleData)
    (json \ "source").as[String] should be("test-format")
    
    val deserialized = json.as[Data]
    deserialized.source should be(sampleData.source)
    deserialized.mostCommonWords should contain ("line1", 1)
  }

    "fully exercise the manual dataFormat round-trip" in {
    val fileIO = new JsonFileIO()
    import fileIO.dataFormat
    
    val sample = Data(
      "src", List("orig"), List("disp"), 10, 2, 
      List(("word", 1)), List("lib"), 5, List("img"), List("lnk")
    )

    val json = Json.toJson(sample)
    (json \ "source").as[String] shouldBe "src"
    (json \ "complexity").as[Int] shouldBe 5

    val result = json.as[Data]
    result shouldBe sample
  }
  }
}