package de.htwg.webscraper.model.analyzer

import de.htwg.webscraper.model.data.ProjectData
import scala.io.Source
import scala.util.Using
import scala.util.{Try, Success, Failure}


trait Analyzer {
  def process(original: List[String], filtered: List[String] = Nil): ProjectData
}

