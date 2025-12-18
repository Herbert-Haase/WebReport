package de.htwg.webscraper.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class DataSpec extends AnyWordSpec with Matchers {
  "A Data object" should {
    "be created from a filtered context with updated stats" in {
      val originalLines = List("This is the complete text.", "It has several lines.", "Complete and final.")
      val filteredLines = List("This is the complete text.", "Complete and final.")
      val data = Data.fromFiltered(originalLines, filteredLines)

      data.characterCount should be(46)
      data.wordCount should be(8)
      data.mostCommonWords should contain theSameElementsAs List(("complete", 2), ("and", 1), ("final", 1), ("is", 1), ("text", 1))
    }

    "be created correctly from new content (fromContent)" in {
      val lines = List("New content line.", "Another content line.")
      
      val data = Data.fromContent(lines)

      data.originalLines should be(lines)
      data.displayLines should be(lines)
      
      data.characterCount should be(39)
      data.wordCount should be(6)
      data.mostCommonWords should contain (("content", 2))
    }
  }
}