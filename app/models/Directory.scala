package models

case class Directory(id: Long,
                     name: String,
                     label: String,
                     description: String,
                     order: Int,
                     relativePath: String,
                     path: String,
                     branchId: Long,
                     var pages: Seq[Page] = Seq(),
                     var children: Seq[Directory] = Seq(),
                     var parent: Option[Directory] = None,
                     var externalId: Option[Long] = None ){

  def isRoot() : Boolean = this.relativePath == "/"

  override def toString: String = s"Directory{id:${id}, name:${name}, relativePath:${relativePath}, path:${path}, parent:${parent.map(_.id)}, externalId:${externalId}}"
}
