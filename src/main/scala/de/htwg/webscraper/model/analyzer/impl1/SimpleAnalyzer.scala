package de.htwg.webscraper.model.analyzer.impl1.simpleAnalyzer

import de.htwg.webscraper.model.analyzer.Analyzer
import de.htwg.webscraper.model.data.ProjectData
import de.htwg.webscraper.model.data.impl1.Data

class SimpleAnalyzer extends Analyzer {
  
  override def process(original: List[String], filtered: List[String] = Nil): ProjectData = {
    if (filtered.isEmpty) {
      Data.fromContent(original)
    } else {
      Data.fromFiltered(original, filtered)
    }
  }
}