package de.htwg.webscraper.model.data

trait ProjectData {
  def originalLines: List[String]
  def displayLines: List[String]
  def characterCount: Int
  def wordCount: Int
  def mostCommonWords: List[(String, Int)]
  def libraries: List[String]
  def complexity: Int
  def lineCount: Int
  def imageCount: Int
  def linkCount: Int
}

object ProjectData {
}