package models

case class Page(id: Long, name: String, label: String, description: String, order: Int, markdown: Option[String], relativePath: String, path: String, directoryId: Long)
