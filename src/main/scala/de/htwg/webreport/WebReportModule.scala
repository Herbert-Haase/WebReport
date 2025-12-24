package de.htwg.webreport

import com.google.inject.AbstractModule
import de.htwg.webreport.controller.sessionManager.SessionManagerTrait
import de.htwg.webreport.controller.sessionManager.impl1.SessionManager
import de.htwg.webreport.model.fileio.FileIOTrait
import de.htwg.webreport.model.fileio.implXML.XmlFileIO 
// import de.htwg.webreport.model.fileio.implJSON.JsonFileIO
import de.htwg.webreport.model.analyzer.AnalyzerTrait
import de.htwg.webreport.model.analyzer.impl1.SimpleAnalyzer
import de.htwg.webreport.model.webClient.WebClientTrait
import de.htwg.webreport.model.webClient.impl1.SimpleWebClient

class WebScraperModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[AnalyzerTrait]).to(classOf[SimpleAnalyzer])
    bind(classOf[WebClientTrait]).to(classOf[SimpleWebClient])

    bind(classOf[SessionManagerTrait]).to(classOf[SessionManager])

    // bind(classOf[FileIOTrait]).to(classOf[JsonFileIO])
    bind(classOf[FileIOTrait]).to(classOf[XmlFileIO])
  }
}