package de.htwg.webscraper.model

trait ProjectData {
  def originalLines: List[String]
  def displayLines: List[String]
  def characterCount: Int
  def wordCount: Int
  def mostCommonWords: List[(String, Int)]
}

case class Data(
    originalLines: List[String],
    displayLines: List[String],
    characterCount: Int,
    wordCount: Int,
    mostCommonWords: List[(String, Int)]
) extends ProjectData

object Data {

  def apply(lines: List[String]): Data = {
    val stats = calculateStats(lines)
    Data(lines, lines, stats._1, stats._2, stats._3)
  }

  private def calculateStats(lines: List[String]): (Int, Int, List[(String, Int)]) = {
    val text = lines.mkString("\n")
    val words = text.toLowerCase.replaceAll("[^a-z]+", " ").trim.split("\\s+").filter(_.nonEmpty)
    val wordFrequencies = words.groupMapReduce(identity)(_ => 1)(_ + _)
    val mostCommon = wordFrequencies.toList.sortBy { case (word, count) => (-count, word) }.take(5)
    (text.length, words.length, mostCommon)
  }

  // Use this for new content
  def fromContent(lines: List[String]): Data = {
    val (charCount, wordCount, commonWords) = calculateStats(lines)
    Data(lines, lines, charCount, wordCount, commonWords)
  }

  // Use this for filtered updates
  def fromFiltered(original: List[String], filtered: List[String]): Data = {
    val (charCount, wordCount, commonWords) = calculateStats(filtered)
    Data(original, filtered, charCount, wordCount, commonWords)
  }
}