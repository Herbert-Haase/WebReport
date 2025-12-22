package de.htwg.webscraper.controller

import de.htwg.webscraper.model.data.ProjectData
import de.htwg.webscraper.util.Observable

trait ControllerInterface extends Observable {
  def data: ProjectData
  def downloadFromUrl(url: String): Unit
  def loadFromFile(path: String): Unit
  def loadFromText(text: String): Unit
  def saveSession(path: String): Unit 
  def filter(word: String): Unit
  def undo(): Unit
  def redo(): Unit
  def reset(): Unit
  def storageMode: String
}