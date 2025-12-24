package de.htwg.webreport.model.data

trait DataTrait {
  def source: String
  def originalLines: List[String]
  def displayLines: List[String]
  def characterCount: Int
  def wordCount: Int
  def mostCommonWords: List[(String, Int)]
  def libraries: List[String]
  def complexity: Int
  def imageCount: Int
  def linkCount: Int
  def images: List[String]
  def links: List[String]
  def lineCount: Int = displayLines.length
}

object DataTrait {
}