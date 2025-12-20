package de.htwg.webscraper.controller

import de.htwg.webscraper.model.webClient.WebClient
import de.htwg.webscraper.model.analyzer.Analyzer
import de.htwg.webscraper.util.{Command, Memento, Originator, UndoManager}
import _root_.de.htwg.webscraper.model.data.ProjectData
import de.htwg.webscraper.util.Observable
import scala.util.{Try, Success, Failure}
import scala.io.Source
import scala.util.Using
import scala.compiletime.uninitialized
import de.htwg.webscraper.model.data.ProjectData

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