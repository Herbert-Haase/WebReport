package de.htwg.webscraper.controller.exporter.impl1.jsonExporter

import de.htwg.webscraper.model.data.ProjectData
import de.htwg.webscraper.controller.exporter.Exporter
import java.io.{File, BufferedWriter, FileWriter}
import scala.util.Try

class JsonExporter extends Exporter {
  override def exportData(data: ProjectData, filePath: String): Try[String] = Try {
    val json = s"""{
      |  "stats": {
      |    "characterCount": ${data.characterCount},
      |    "wordCount": ${data.wordCount}
      |  }
      |}""".stripMargin

    val file = new File(filePath)
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(json)
    bw.close()
    s"Successfully exported to $filePath"
  }
}