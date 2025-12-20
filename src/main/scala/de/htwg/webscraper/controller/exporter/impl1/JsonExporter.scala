package de.htwg.webscraper.controller.exporter.impl1.jsonExporter

import de.htwg.webscraper.model.data.ProjectData
import de.htwg.webscraper.controller.exporter.Exporter
import java.io.{File, BufferedWriter, FileWriter}
import scala.util.Try

class JsonExporter extends Exporter {
  override def exportData(data: ProjectData, filePath: String): Try[String] = Try {
    val topWordsJson = data.mostCommonWords.map { case (w, c) => 
      s"""      {"word": "$w", "count": $c}""" 
    }.mkString(",\n")

    val libsJson = data.libraries.map(l => s"""      "$l"""").mkString(",\n")
    val imagesJson = data.images.map(i => s"""      "$i"""").mkString(",\n")
    val linksJson = data.links.map(l => s"""      "$l"""").mkString(",\n")

    val json = s"""{
  "analysis": {
    "meta": {
      "characterCount": ${data.characterCount},
      "wordCount": ${data.wordCount},
      "lineCount": ${data.lineCount},
      "complexity": ${data.complexity},
      "images": {
        "count": ${data.imageCount},
        "list": [
$imagesJson
        ]
      },
      "links": {
        "count": ${data.linkCount},
        "list": [
$linksJson
        ]
      }
    },
    },
    "anatomy": {
      "libraries": [
$libsJson
      ]
    },
    "content": {
      "topWords": [
$topWordsJson
      ]
    }
  }
}"""

    val file = new File(filePath)
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(json)
    bw.close()
    s"Successfully exported JSON to $filePath"
  }
}