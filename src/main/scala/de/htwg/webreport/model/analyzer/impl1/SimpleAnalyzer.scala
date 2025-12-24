package de.htwg.webreport.model.analyzer.impl1

import de.htwg.webreport.model.analyzer.AnalyzerTrait
import de.htwg.webreport.model.data.DataTrait
import de.htwg.webreport.model.data.impl1.Data

class SimpleAnalyzer extends AnalyzerTrait {
  override def process(original: List[String], filtered: Option[List[String]], source: String): DataTrait = {
    filtered match {
      case Some(lines) => Data.fromFiltered(original, lines, source)
      case None        => Data.fromContent(original, source)
    }
  }
}