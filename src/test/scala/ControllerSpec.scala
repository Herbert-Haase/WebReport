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
  }
    "handle loadFromFile success case" in {
      // Create temp file
      val file = File.createTempFile("test_scraper", ".txt")
      file.deleteOnExit()
      new PrintWriter(file) { write("Hello File"); close() }

      val controller = new Controller()
      controller.loadFromFile(file.getAbsolutePath)

      // Covers LoadCommand {28-31} Success path
      controller.data.displayLines should be(List("Hello File"))
    }

"handle loadFromFile failure case" in {
  val controller = new Controller()
  // Covers LoadCommand failure path
  controller.loadFromFile("non_existent_file_XYZ.txt")

  val head = controller.data.displayLines.head
  head should startWith ("Error:")
  head should include ("non_existent_file_XYZ.txt")
}

    "handle reset" in {
      val controller = new Controller()
      controller.loadFromText("Data")

      // Covers reset {61-64}
      controller.reset()
      controller.data.displayLines should be(empty)
    }
  "The UndoManager" should {
    "handle empty stacks safely" in {
      val controller = new Controller()

      // Covers UndoManager case Nil for undoStep
      noException should be thrownBy controller.undo()

      // Covers UndoManager case Nil for redoStep
      noException should be thrownBy controller.redo()
    }
  }
}