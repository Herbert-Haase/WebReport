package de.htwg.webscraper.controller.exporter

import de.htwg.webscraper.model.data.ProjectData
import scala.util.Try

trait Exporter {
  def exportData(data: ProjectData, filePath: String): Try[String]
}