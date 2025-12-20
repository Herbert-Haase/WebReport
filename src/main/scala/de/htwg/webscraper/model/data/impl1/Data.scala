package de.htwg.webscraper.model.data.impl1

import de.htwg.webscraper.model.data.ProjectData

case class Data(
    originalLines: List[String],
    displayLines: List[String],
    characterCount: Int,
    wordCount: Int,
    mostCommonWords: List[(String, Int)],
    libraries: List[String],
    complexity: Int,
    lineCount: Int,
    imageCount: Int,
    linkCount: Int
) extends ProjectData

object Data {

  def apply(lines: List[String]): Data = {
    val stats = calculateStats(lines)
    Data(lines, lines, stats._1, stats._2, stats._3, stats._4, stats._5, stats._6, stats._7, stats._8)
  }

  def fromContent(lines: List[String]): Data = apply(lines)

  def fromFiltered(original: List[String], filtered: List[String]): Data = {
    val stats = calculateStats(filtered)
    Data(original, filtered, stats._1, stats._2, stats._3, stats._4, stats._5, stats._6, stats._7, stats._8)
  }

  private def calculateStats(lines: List[String]): (Int, Int, List[(String, Int)], List[String], Int, Int, Int, Int) = {
    val text = lines.mkString("\n")
    
    val words = text.toLowerCase.replaceAll("[^a-z]+", " ").trim.split("\\s+").filter(_.nonEmpty)
    val wordFrequencies = words.groupMapReduce(identity)(_ => 1)(_ + _)
    val mostCommon = wordFrequencies.toList.sortBy { case (w, c) => (-c, w) }.take(5)

    // Matches: <script src="..."> OR <link ... href="..."> OR import ...
    val webLibRegex = """(?i)<(script|link)[^>]+(src|href)=["']([^"']+)["']""".r
    val codeLibRegex = """^(import|using|#include)\s+(\S+)""".r

    val webLibs = lines.flatMap { line =>
      webLibRegex.findAllMatchIn(line).map(_.group(3))
    }
    
    val codeLibs = lines.map(_.trim).filter(l => l.startsWith("import ") || l.startsWith("using ") || l.startsWith("#include"))
      .map(_.split(" ").lastOption.getOrElse("?"))

    val allLibs = (webLibs ++ codeLibs).map(_.split("/").last).distinct.filter(_.nonEmpty)

    val controlKeywords = Set("if", "else", "for", "while", "case", "catch", "match", "try")
    val complexityScore = words.count(w => controlKeywords.contains(w))
    
    val imageCount = lines.mkString.split("<img ").length - 1 max 0
    val linkCount = lines.mkString.split("<a ").length - 1 max 0

    (text.length, words.length, mostCommon, allLibs, complexityScore, lines.length, imageCount, linkCount)
  }
}