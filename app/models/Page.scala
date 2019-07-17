package models

case class Page(id: Long,
                name: String,
                label: String,
                description: String,
                order: Int,
                markdown: String,
                relativePath: String,
                path: String,
                directoryId: Long)
