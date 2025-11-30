package de.htwg.webscraper.model

case class Data(
    originalLines: List[String],
    displayLines: List[String],
    characterCount: Int,
    wordCount: Int,
    mostCommonWords: List[(String, Int)]
)

object Data {
  private def calculateStats(lines: List[String]): (Int, Int, List[(String, Int)]) = {
    val text = lines.mkString("\n")
    val characterCount = text.length

    val words = text
      .toLowerCase
      .replaceAll("[^a-z]+", " ")
      .trim
      .split("\\s+")
      .filter(_.nonEmpty)

    val wordCount = words.length
    val wordFrequencies = words.groupMapReduce(identity)(_ => 1)(_ + _)

    val mostCommon = wordFrequencies.toList
      .sortBy { case (word, count) => (-count, word) }
      .take(5)

    (characterCount, wordCount, mostCommon)
  }

  def apply(lines: List[String]): Data = {
    val (charCount, wordCount, commonWords) = calculateStats(lines)
    Data(lines, lines, charCount, wordCount, commonWords)
  }

  def fromFiltered(original: List[String], filtered: List[String]): Data = {
    val (charCount, wordCount, commonWords) = calculateStats(filtered)
    Data(original, filtered, charCount, wordCount, commonWords)
  }
}