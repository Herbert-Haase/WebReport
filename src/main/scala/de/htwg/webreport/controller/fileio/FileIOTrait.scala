package de.htwg.webreport.model.fileio

import de.htwg.webreport.model.data.DataTrait

trait FileIOTrait {
  def save(data: List[DataTrait], filePath: String): Unit
  def load(filePath: String): List[DataTrait]
  def mode: String
}