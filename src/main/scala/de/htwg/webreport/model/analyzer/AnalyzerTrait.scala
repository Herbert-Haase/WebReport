package de.htwg.webreport.model.analyzer

import de.htwg.webreport.model.data.DataTrait
import scala.io.Source
import scala.util.Using
import scala.util.{Try, Success, Failure}


trait AnalyzerTrait {
  def process(original: List[String], filtered: Option[List[String]], source: String): DataTrait
}

