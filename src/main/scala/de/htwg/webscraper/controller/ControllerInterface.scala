package de.htwg.webscraper.controller

// Use _root_ to avoid the relative package conflict shown in your error log
import _root_.de.htwg.webscraper.model.ProjectData
import de.htwg.webscraper.util.Observable

trait ControllerInterface extends Observable {
  def data: ProjectData
  def loadFromFile(path: String): Unit
  def loadFromText(text: String): Unit
  def downloadFromUrl(url: String): Unit
  def filter(word: String): Unit
  def undo(): Unit
  def redo(): Unit
  def reset(): Unit
}