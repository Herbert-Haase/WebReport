package de.htwg.webreport.model.data.impl1

import de.htwg.webreport.model.data.DataTrait

case class Data(
    source: String,
    originalLines: List[String],
    displayLines: List[String],
    characterCount: Int,
    wordCount: Int,
    mostCommonWords: List[(String, Int)],
    libraries: List[String],
    complexity: Int,
    images: List[String],
    links: List[String]
) extends DataTrait {
  override def imageCount: Int = images.length
  override def linkCount: Int = links.length
}

object Data {
  def apply(lines: List[String], src: String): Data = {
    val stats = calculateStats(lines)
    Data(src, lines, lines, stats._1, stats._2, stats._3, stats._4, stats._5, stats._6, stats._7)
  }

  def fromContent(lines: List[String], source: String): Data = apply(lines, source)

  def fromFiltered(original: List[String], filtered: List[String], source: String): Data = {
    val stats = calculateStats(filtered)
    Data(source, original, filtered, stats._1, stats._2, stats._3, stats._4, stats._5, stats._6, stats._7)
  }

  def restore(
      source: String, originalLines: List[String], displayLines: List[String],
      chars: Int, words: Int, topWords: List[(String, Int)],
      libs: List[String], complexity: Int, imgs: List[String], links: List[String]
  ): Data = {
    Data(source, originalLines, displayLines, chars, words, topWords, libs, complexity, imgs, links)
  }

  private def calculateStats(lines: List[String]): (Int, Int, List[(String, Int)], List[String], Int, List[String], List[String]) = {
    val text = lines.mkString("\n")
    val words = text.toLowerCase.replaceAll("[^a-z]+", " ").trim.split("\\s+").filter(_.nonEmpty)
    
    // Stats
    val mostCommon = if (words.isEmpty) Nil else words.groupMapReduce(identity)(_ => 1)(_ + _).toList.sortBy(w => (-w._2, w._1)).take(5)
    
    // Regex Extraction
    val webLibRegex = """(?i)<(script|link)[^>]+(src|href)=["']([^"']+)["']""".r
    val codeLibRegex = """^(import|using|#include)\s+(\S+)""".r

    val webLibs = lines.flatMap(l => webLibRegex.findAllMatchIn(l).map(_.group(3)))
    val codeLibs = lines.map(_.trim)
      .filter(l => l.startsWith("import ") || l.startsWith("using "))
      .map(_.split(" ").lastOption.getOrElse("?"))

    val allLibs = (webLibs ++ codeLibs)
      .map(_.split("/").last)         
      .map(_.takeWhile(_ != '?'))     
      .map(_.replaceAll("""(\.min)?\.(js|css)$""", ""))
      .map(_.split("\\.").last)
      .map(_.replaceAll("""[-._][0-9a-fA-F]{8,}.*$""", ""))
      .distinct
      .filter(_.length > 1)
    val keywordCount = words.count(w => Set("if", "else", "for", "while", "case", "catch", "match", "try").contains(w))
    val complexityScore = if (words.isEmpty) 0 else ((keywordCount.toDouble / words.length) * 1000).toInt
    val imgRegex = """<img[^>]+src=["']([^"']+)["']""".r
    val linkRegex = """<a[^>]+href=["']([^"']+)["']""".r

    val foundImages = lines.flatMap(l => imgRegex.findAllMatchIn(l).map(_.group(1)))
    val foundLinks = lines.flatMap(l => linkRegex.findAllMatchIn(l).map(_.group(1)))

    (text.length, words.length, mostCommon, allLibs, complexityScore, foundImages, foundLinks)
  }
}