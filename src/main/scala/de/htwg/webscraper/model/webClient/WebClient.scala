package de.htwg.webscraper.model.webClient

import scala.io.Source
import scala.util.Using
import scala.util.{Try, Success, Failure}

// Interface for downloading
trait WebClient {
  def get(url: String): Try[String]
  def download(url: String): Try[String]
}