package de.htwg.webscraper.model.fileio.impl

import de.htwg.webscraper.model.fileio.FileIO
import de.htwg.webscraper.model.data.ProjectData
import de.htwg.webscraper.model.data.impl1.Data
import play.api.libs.json._
import java.io.{File, PrintWriter}
import scala.io.Source

class JsonFileIO extends FileIO {
  override val mode: String = "JSON"

  implicit val wordFormat: Format[(String, Int)] = new Format[(String, Int)] {
    def writes(o: (String, Int)): JsValue = Json.obj("word" -> o._1, "count" -> o._2)
    def reads(json: JsValue): JsResult[(String, Int)] = for {
      w <- (json \ "word").validate[String]
      c <- (json \ "count").validate[Int]
    } yield (w, c)
  }

  implicit val dataFormat: OFormat[Data] = Json.format[Data]

  override def save(dataList: List[ProjectData], filePath: String): Unit = {
    val concreteList = dataList.map {
      case d: Data => d
      case other => Data.restore(
        other.source, other.originalLines, other.displayLines, other.characterCount,
        other.wordCount, other.mostCommonWords, other.libraries, other.complexity, other.images, other.links
      )
    }
    
    val json = Json.prettyPrint(Json.toJson(concreteList))
    val pw = new PrintWriter(new File(filePath))
    pw.write(json)
    pw.close()
  }

  override def load(filePath: String): List[ProjectData] = {
    val fileContent = Source.fromFile(filePath).getLines.mkString
    val json = Json.parse(fileContent)
    json.validate[List[Data]] match {
      case JsSuccess(list, _) => list
      case JsError(errors) => 
        println(s"JSON Parse Error: $errors")
        Nil
    }
  }
}