package de.htwg.webscraper.model.fileio

import de.htwg.webscraper.model.data.ProjectData

trait FileIO {
  def save(data: List[ProjectData], filePath: String): Unit
  def load(filePath: String): List[ProjectData]
  def mode: String
}