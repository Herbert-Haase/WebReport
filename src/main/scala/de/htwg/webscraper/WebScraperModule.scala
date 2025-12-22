package de.htwg.webscraper

import com.google.inject.AbstractModule
import de.htwg.webscraper.controller.ControllerInterface
import de.htwg.webscraper.controller.impl1.controller.Controller
import de.htwg.webscraper.model.fileio.FileIO
import de.htwg.webscraper.model.fileio.impl.XmlFileIO 
// import de.htwg.webscraper.model.fileio.impl.JsonFileIO
import de.htwg.webscraper.model.analyzer.Analyzer
import de.htwg.webscraper.model.analyzer.impl1.simpleAnalyzer.SimpleAnalyzer
import de.htwg.webscraper.model.webClient.WebClient
import de.htwg.webscraper.model.webClient.impl1.simpleWebClient.SimpleWebClient

class WebScraperModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[Analyzer]).to(classOf[SimpleAnalyzer])
    bind(classOf[WebClient]).to(classOf[SimpleWebClient])

    bind(classOf[ControllerInterface]).to(classOf[Controller])

    // bind(classOf[FileIO]).to(classOf[JsonFileIO])
    bind(classOf[FileIO]).to(classOf[XmlFileIO])
  }
}