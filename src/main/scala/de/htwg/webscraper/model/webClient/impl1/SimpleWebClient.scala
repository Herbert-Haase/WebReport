package de.htwg.webscraper.model.webClient.impl1.simpleWebClient

import de.htwg.webscraper.model.webClient.WebClient

import scala.io.Source
import scala.util.Using
import scala.util.{Try, Success, Failure}

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