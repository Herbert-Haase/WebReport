package de.htwg.webscraper.model.analyzer.impl1.simpleAnalyzer

import de.htwg.webscraper.model.analyzer.Analyzer
import de.htwg.webscraper.model.data.ProjectData
import scala.io.Source
import scala.util.Using
import scala.util.{Try, Success, Failure}

// Concrete Implementation
class SimpleAnalyzer extends Analyzer {
  override def process(original: List[String], filtered: List[String] = Nil): ProjectData = {
    val linesToAnalyze = if (filtered.isEmpty) original else filtered

    val text = linesToAnalyze.mkString("\n")
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

    ProjectData(original, linesToAnalyze, characterCount, wordCount, mostCommon)
  }
}
