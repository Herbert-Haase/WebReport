package de.htwg.webscraper.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

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
}