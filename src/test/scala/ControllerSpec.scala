package de.htwg.webscraper.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import java.io.{File, PrintWriter}

class ControllerSpec extends AnyWordSpec with Matchers {
  "A Controller" should {
    "load text correctly via Command" in {
      val controller = new Controller()
      controller.loadFromText("Hello\nWorld")
      controller.data.displayLines should be(List("Hello", "World"))
      controller.data.wordCount should be(2)
    }

    "filter text correctly via Command" in {
      val controller = new Controller()
      controller.loadFromText("Apple\nBanana\nApricot")
      controller.filter("Ap")

      controller.data.displayLines should contain("Apple")
      controller.data.displayLines should contain("Apricot")
      controller.data.displayLines should not contain("Banana")
    }

    "Undo a filter correctly" in {
      val controller = new Controller()
      controller.loadFromText("One\nTwo\nThree")

      controller.filter("One")
      controller.data.displayLines should be(List("One"))

      controller.undo()
      controller.data.displayLines should be(List("One", "Two", "Three"))
    }

    "Undo a load correctly (returning to empty)" in {
       val controller = new Controller()
       controller.loadFromText("Data")
       controller.undo()
       controller.data.displayLines should be(empty)
    }

    "Redo a filter correctly" in {
      val controller = new Controller()
      controller.loadFromText("A\nB\nC")

      controller.filter("B")
      controller.undo() // Back to A, B, C
      controller.redo() // Redo Filter

      controller.data.displayLines should be(List("B"))
    }

    "Redo a load correctly" in {
      val controller = new Controller()
      controller.loadFromText("Initial Load")
      controller.undo() // Data empty
      controller.redo() // Should reload "Initial Load"

      controller.data.displayLines should be(List("Initial Load"))
    }
  
    // --- NEW TESTS START HERE ---

    "handle downloadFromUrl success case" in {
      // 1. Create a temp file to simulate a remote resource
      val file = File.createTempFile("download_test", ".txt")
      file.deleteOnExit()
      new PrintWriter(file) { write("Downloaded Content"); close() }
      
      // 2. Convert file path to a URL (file://...)
      val url = file.toURI.toURL.toString
      
      val controller = new Controller()
      controller.downloadFromUrl(url)
      
      // 3. Verify content was "downloaded"
      controller.data.displayLines should be(List("Downloaded Content"))
    }

    "handle downloadFromUrl failure case" in {
      val controller = new Controller()
      // 1. Try to download from a non-existent/invalid URL
      controller.downloadFromUrl("http://invalid-url-xyz.test")
      
      // [cite_start]// 2. Verify error handling in Controller.scala [cite: 1]
      val head = controller.data.displayLines.head
      head should startWith ("Error downloading from")
    }

    "Undo and Redo a download correctly" in {
      // Setup mock download
      val file = File.createTempFile("undo_redo_test", ".txt")
      file.deleteOnExit()
      new PrintWriter(file) { write("New Data"); close() }
      val url = file.toURI.toURL.toString

      val controller = new Controller()
      controller.loadFromText("Old Data")
      
      // 1. Execute Download
      controller.downloadFromUrl(url)
      controller.data.displayLines should be(List("New Data"))
      
      // 2. Undo -> Should revert to "Old Data"
      controller.undo()
      controller.data.displayLines should be(List("Old Data"))
      
      // 3. Redo -> Should re-fetch "New Data"
      controller.redo()
      controller.data.displayLines should be(List("New Data"))
    }

    // --- NEW TESTS END HERE ---

    "handle loadFromFile success case" in {
      val file = File.createTempFile("test_scraper", ".txt")
      file.deleteOnExit()
      new PrintWriter(file) { write("Hello File"); close() }

      val controller = new Controller()
      controller.loadFromFile(file.getAbsolutePath)

      controller.data.displayLines should be(List("Hello File"))
    }

    "handle loadFromFile failure case" in {
      val controller = new Controller()
      controller.loadFromFile("non_existent_file_XYZ.txt")

      val head = controller.data.displayLines.head
      head should startWith ("Error:")
      head should include ("non_existent_file_XYZ.txt")
    }

    "handle reset" in {
      val controller = new Controller()
      controller.loadFromText("Data")
      controller.reset()
      controller.data.displayLines should be(empty)
    }
  }
  
  "The UndoManager" should {
    "handle empty stacks safely" in {
      val controller = new Controller()
      noException should be thrownBy controller.undo()
      noException should be thrownBy controller.redo()
    }
  }
}