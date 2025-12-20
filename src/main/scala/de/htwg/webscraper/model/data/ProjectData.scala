package de.htwg.webscraper.model.data

trait ProjectData {
  def originalLines: List[String]
  def displayLines: List[String]
  def characterCount: Int
  def wordCount: Int
  def mostCommonWords: List[(String, Int)]
}

object ProjectData {
  def apply(
      originalLines: List[String],
      displayLines: List[String],
      characterCount: Int,
      wordCount: Int,
      mostCommonWords: List[(String, Int)]
  ): ProjectData = {
    ProjectDataImpl(originalLines, displayLines, characterCount, wordCount, mostCommonWords)
  }

  private case class ProjectDataImpl(
      originalLines: List[String],
      displayLines: List[String],
      characterCount: Int,
      wordCount: Int,
      mostCommonWords: List[(String, Int)]
  ) extends ProjectData
}