package de.htwg.webscraper.model

import de.htwg.webscraper.model.Data
import scala.io.Source
import scala.util.Using
import scala.util.{Try, Success, Failure}

// Interface for downloading
trait WebClient {
  def get(url: String): Try[String]
  def download(url: String): Try[String]
}

// Concrete Implementation
class SimpleWebClient extends WebClient {
  override def get(url: String): Try[String] = Try {
    val source = Source.fromURL(url)
    val content = source.mkString
    source.close()
    content
  }
  def download(url: String): Try[String] =
  Try {
    Using.resource(Source.fromURL(url))(_.mkString)
  }
}

// Interface for analyzing text
trait Analyzer {
  def process(original: List[String], filtered: List[String] = Nil): Data
}

// Concrete Implementation
class SimpleAnalyzer extends Analyzer {
  override def process(original: List[String], filtered: List[String] = Nil): Data = {
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

    Data(original, linesToAnalyze, characterCount, wordCount, mostCommon)
  }
}
