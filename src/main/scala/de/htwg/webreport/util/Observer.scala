package de.htwg.webreport.util

trait Observable {
  protected var subscribers: Vector[Observer] = Vector()
  def add(s: Observer): Unit = subscribers = subscribers :+ s
  def remove(s: Observer): Unit = subscribers = subscribers.filterNot(_ == s)
  def notifyObservers(isFilterUpdate: Boolean = false): Unit = subscribers.foreach(_.update(isFilterUpdate))
}

trait Observer {
  def update(isFilterUpdate: Boolean = false): Unit
}