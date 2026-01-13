package de.htwg.webreport.model.fileio.implXML

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.webreport.model.data.impl1.Data
import java.io.File

class XmlFileIOSpec extends AnyWordSpec with Matchers {
    "XmlFileIO" should {
        "correctly save and load lists of libraries, images, and links" in {
            val fileIO = new de.htwg.webreport.model.fileio.implXML.XmlFileIO()
            val tempFile = java.io.File.createTempFile("full_test", ".xml")
            
            val fullData = Data(
            source = "xml-test",
            originalLines = List("test"),
            displayLines = List("test"),
            characterCount = 4,
            wordCount = 1,
            mostCommonWords = List(("test", 1)),
            libraries = List("react.js"),
            complexity = 1,
            images = List("logo.png"),
            links = List("https://google.com")
            )

            try {
            fileIO.save(List(fullData), tempFile.getAbsolutePath)
            val loaded = fileIO.load(tempFile.getAbsolutePath)
            val result = loaded.head
            
            result.libraries should contain ("react.js")
            result.images should contain ("logo.png")
            result.links should contain ("https://google.com")
            } finally { 
            if (tempFile.exists()) tempFile.delete() 
            }
        }
    }

}