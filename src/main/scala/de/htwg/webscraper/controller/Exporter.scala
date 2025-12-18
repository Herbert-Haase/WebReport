package de.htwg.webscraper.controller

import de.htwg.webscraper.model.ProjectData
import java.io.{File, BufferedWriter, FileWriter}
import scala.util.Try

trait Exporter {
  def exportData(data: ProjectData, filePath: String): Try[String]
}

class JsonExporter extends Exporter {
  override def exportData(data: ProjectData, filePath: String): Try[String] = Try {
    val json = new StringBuilder()
    json.append("{\n")
    json.append(s"""  "stats": {\n""")
    json.append(s"""    "characterCount": ${data.characterCount},\n""")
    json.append(s"""    "wordCount": ${data.wordCount}\n""")
    json.append("  }\n")
    json.append("}")
    
    val file = new File(filePath)
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(json.toString())
    bw.close()
    s"Successfully exported to $filePath"
  }
}