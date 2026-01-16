package de.htwg.webreport.model.fileio.implJSON

import de.htwg.webreport.model.fileio.FileIOTrait
import de.htwg.webreport.model.data.DataTrait
import de.htwg.webreport.model.data.impl1.Data
import play.api.libs.json._
import java.io.{File, PrintWriter}
import scala.io.Source

class JsonFileIO extends FileIOTrait {
  override val mode: String = "JSON"

  implicit val wordFormat: Format[(String, Int)] = new Format[(String, Int)] {
    def writes(o: (String, Int)): JsValue = Json.obj("word" -> o._1, "count" -> o._2)
    def reads(json: JsValue): JsResult[(String, Int)] = for {
      w <- (json \ "word").validate[String]
      c <- (json \ "count").validate[Int]
    } yield (w, c)
  }

  implicit val dataFormat: OFormat[Data] = new OFormat[Data] {
    override def writes(d: Data): JsObject = Json.obj(
      "source" -> d.source,
      "originalLines" -> d.originalLines,
      "displayLines" -> d.displayLines,
      "characterCount" -> d.characterCount,
      "wordCount" -> d.wordCount,
      "mostCommonWords" -> d.mostCommonWords,
      "libraries" -> d.libraries,
      "complexity" -> d.complexity,
      "images" -> d.images,
      "links" -> d.links
    )

    override def reads(json: JsValue): JsResult[Data] = for {
      source <- (json \ "source").validate[String]
      originalLines <- (json \ "originalLines").validate[List[String]]
      displayLines <- (json \ "displayLines").validate[List[String]]
      characterCount <- (json \ "characterCount").validate[Int]
      wordCount <- (json \ "wordCount").validate[Int]
      mostCommonWords <- (json \ "mostCommonWords").validate[List[(String, Int)]]
      libraries <- (json \ "libraries").validate[List[String]]
      complexity <- (json \ "complexity").validate[Int]
      images <- (json \ "images").validate[List[String]]
      links <- (json \ "links").validate[List[String]]
    } yield Data(source, originalLines, displayLines, characterCount, wordCount, mostCommonWords, libraries, complexity, images, links)
  }

  override def save(dataList: List[DataTrait], filePath: String): Unit = {
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

  override def load(filePath: String): List[DataTrait] = {
    val file = new File(filePath)
    if (!file.exists()) throw new java.io.FileNotFoundException(s"File not found: $filePath")
    
    val source = Source.fromFile(file)
    val jsonString = source.getLines().mkString
    source.close()
    
    val json = Json.parse(jsonString)
    json.as[List[Data]]
  }
}