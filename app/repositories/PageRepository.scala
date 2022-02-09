package repositories

import anorm.SqlParser._
import anorm._
import javax.inject.Inject
import models.{Branch, Directory, Page, PageJoinProject, Project, Variable}
import play.api.db.Database
import play.api.libs.json.Json
import repositories.MariaDBImplicits._

class PageRepository @Inject()(db: Database) {
  implicit val variableFormat = Json.format[Variable]

  private val fullJoinProjectParser = for {

    id <- long("id")
    name <- str("name")
    label <- str("label")
    description <- str("description")
    order <- int("order")
    markdown <- str("markdown")
    relativePath <- str("relativePath")
    path <- str("path")
    directoryId <- long("directoryId")
    dependOnOpenApi <- bool("dependOnOpenApi")

    d_id <- long("d_id")
    d_name <- str("d_name")
    d_label <- str("d_label")
    d_description <- str("d_description")
    d_order <- int("d_order")
    d_relativePath <- str("d_relativePath")
    d_path <- str("d_path")
    d_branchId <- long("d_branchId")

    b_id <- long("b_id")
    b_name <- str("b_name")
    b_isStable <- bool("b_isStable")
    b_projectId <- str("b_projectId")

    p_id <- str("p_id")
    p_name <- str("p_name")
    p_repositoryUrl <- str("p_repositoryUrl")
    p_sourceUrlTemplate <- str("p_sourceUrlTemplate").?
    p_stableBranch <- str("p_stableBranch")
    p_displayedBranches <- str("p_displayedBranches").?
    p_featuresRootPath <- str("p_featuresRootPath").?
    p_documentationRootPath <- str("p_documentationRootPath").?
    p_variables <- str("p_variables").?
  } yield
    PageJoinProject(
      Page(id, name, label, description, order, Some(markdown), relativePath, path, directoryId, dependOnOpenApi),
      Directory(d_id, d_name, d_label, d_description, d_order, d_relativePath, d_path, d_branchId),
      Branch(b_id, b_name, b_isStable, b_projectId),
      Project(p_id, p_name, p_repositoryUrl, p_sourceUrlTemplate, p_stableBranch, p_displayedBranches, p_featuresRootPath, p_documentationRootPath, p_variables.map(Json.parse(_).as[Seq[Variable]]))
    )


  private val fullParser = for {
    id <- long("id")
    name <- str("name")
    label <- str("label")
    description <- str("description")
    order <- int("order")
    markdown <- str("markdown")
    relativePath <- str("relativePath")
    path <- str("path")
    directoryId <- long("directoryId")
    dependOnOpenApi <- bool("dependOnOpenApi")
  } yield Page(id, name, label, description, order, Some(markdown), relativePath, path, directoryId, dependOnOpenApi)

  private val parser = for {
    id <- long("id")
    name <- str("name")
    label <- str("label")
    description <- str("description")
    order <- int("order")
    relativePath <- str("relativePath")
    path <- str("path")
    directoryId <- long("directoryId")
    dependOnOpenApi <- bool("dependOnOpenApi")
  } yield Page(id, name, label, description, order, None, relativePath, path, directoryId, dependOnOpenApi)

  def findAllWithContent(): Seq[Page] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM page".as(fullParser.*)
    }
  }

  def findAllByDirectoryIdWithContent(directoryId: Long): Seq[Page] = {
    db.withConnection { implicit connection =>
      SQL"SELECT id, name, label, description, `order`, markdown, relativePath, path, directoryId, dependOnOpenApi FROM page WHERE directoryId = $directoryId order by `order` ".as(fullParser.*)
    }
  }

  def findAll(): Seq[Page] = {
    db.withConnection { implicit connection =>
      SQL"SELECT id, name, label, description, `order`, relativePath, path, directoryId, dependOnOpenApi FROM page".as(parser.*)
    }
  }

  def findAllDependOnApi(): Seq[Page] = {
    db.withConnection { implicit connection =>
      SQL"SELECT id, name, label, description, `order`, relativePath, path, directoryId, dependOnOpenApi FROM page WHERE dependOnOpenApi=true".as(parser.*)
    }
  }

  def findAllByProjectId(projectId: String): Seq[Page] = {
    db.withConnection { implicit connection =>
      SQL"""select  pg.*,
                   d.id as d_id,
                   d.name as d_name,
                   d.label as d_label,
                   d.description as d_description,
                   d.`order` as d_order,
                   d.relativePath as d_relativePath,
                   d.path as d_path,
                   d.branchId as d_branchId,
                   b.id as b_id,
                   b.name as b_name,
                   b.isStable as b_isStable,
                   b.projectId as b_projectId,
                   p.id as p_id,
                   p.name as p_name,
                   p.repositoryUrl as p_repositoryUrl,
                   p.stableBranch as p_stableBranch,
                   p.displayedBranches as p_displayedBranches,
                   p.featuresRootPath as p_featuresRootPath,
                   p.documentationRootPath as p_documentationRootPath,
                   p.variables as p_variables
                  from page pg
                      join directory d on d.id = pg.directoryId
                      join branch b on b.id = d.branchId
                      join project p on p.id = b.projectId
                       where p.id = $projectId
               """.as(parser.*)
    }
  }

  def findAllByDirectoryId(directoryId: Long): Seq[Page] = {
    db.withConnection { implicit connection =>
      SQL"SELECT id, name, label, description, `order`, relativePath, path, directoryId, dependOnOpenApi FROM page WHERE directoryId = $directoryId order by `order` ".as(parser.*)
    }
  }

  def save(page: Page): Page = {
    db.withConnection { implicit connection =>

      val id: Option[Long] = findById(page.id).orElse(findByDirectoryIdAndName(page.directoryId, page.name)) match {
        case Some(existingPage) =>
          SQL"""REPLACE INTO page (id, name, label, description, `order`, markdown, relativePath, path, directoryId, dependOnOpenApi)
               VALUES (${existingPage.id},${page.name},${page.label},${page.description},${page.order},${page.markdown},${page.relativePath},${page.path},${page.directoryId},${page.dependOnOpenApi})"""
            .executeUpdate()

          Some(existingPage.id)

        case _ =>
          SQL"""INSERT INTO page (name, label, description, `order`, markdown, relativePath, path, directoryId, dependOnOpenApi)
               VALUES (${page.name},${page.label},${page.description},${page.order},${page.markdown},${page.relativePath},${page.path},${page.directoryId},${page.dependOnOpenApi})"""
            .executeInsert()
      }

      SQL"SELECT * FROM page WHERE id = $id".as(fullParser.single)
    }
  }

  def saveAll(pages: Seq[Page]): Seq[Page] = {
    pages.map(save)
  }

  def count(): Long = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM page".as(scalar[Long].single)
    }
  }

  def deleteById(id: Long): Unit = {
    db.withConnection { implicit connection =>
      SQL"DELETE FROM page WHERE id = $id".executeUpdate()
      ()
    }
  }

  def deleteAll(pages: Seq[Page]): Unit = {
    db.withConnection { implicit connection =>
      SQL"DELETE FROM page WHERE id IN (${pages.map(_.id)})".executeUpdate()
      ()
    }
  }

  def deleteAllByStartingPath(basePath: String): Unit = {
    db.withConnection { implicit connection =>
      SQL(s"DELETE FROM page WHERE path like '$basePath%' ").executeUpdate()
      ()
    }
  }


  def deleteAllByDirectoryId(directoryId: Long): Unit = {
    db.withConnection { implicit connection =>
      SQL"DELETE FROM page WHERE directoryId = $directoryId".executeUpdate()
      ()
    }
  }

  def deleteAll(): Unit = {
    db.withConnection { implicit connection =>
      SQL"TRUNCATE TABLE page".executeUpdate()
      ()
    }
  }

  def delete(page: Page): Unit = {
    deleteById(page.id)
  }

  def findAllById(ids: Seq[Long]): Seq[Page] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM page WHERE id IN ($ids)".as(fullParser.*)
    }
  }


  def findById(id: Long): Option[Page] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM page WHERE id = $id".as(fullParser.*).headOption
    }
  }

  def findByDirectoryIdAndName(directoryId: Long, name: String): Option[Page] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM page WHERE directoryId = $directoryId AND name = $name  order by `order`".as(fullParser.*).headOption
    }
  }

  def existsById(id: Long): Boolean = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM page WHERE id = $id".as(scalar[Long].single) > 0
    }
  }

  def findByPath(path: String): Option[Page] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM page WHERE path = $path  order by `order`".as(fullParser.*).headOption
    }
  }

  def findByPathJoinProject(path: String): Option[PageJoinProject] = {
    db.withConnection { implicit connection =>
      SQL"""select  pg.*,
                   d.id as d_id,
                   d.name as d_name,
                   d.label as d_label,
                   d.description as d_description,
                   d.`order` as d_order,
                   d.relativePath as d_relativePath,
                   d.path as d_path,
                   d.branchId as d_branchId,
                   b.id as b_id,
                   b.name as b_name,
                   b.isStable as b_isStable,
                   b.projectId as b_projectId,
                   p.id as p_id,
                   p.name as p_name,
                   p.repositoryUrl as p_repositoryUrl,
                   p.sourceUrlTemplate as p_sourceUrlTemplate,
                   p.stableBranch as p_stableBranch,
                   p.displayedBranches as p_displayedBranches,
                   p.featuresRootPath as p_featuresRootPath,
                   p.documentationRootPath as p_documentationRootPath,
                   p.variables as p_variables
                  from page pg
                      join directory d on d.id = pg.directoryId
                      join branch b on b.id = d.branchId
                      join project p on p.id = b.projectId
                       where pg.path = $path
               """.as(fullJoinProjectParser.*).headOption
    }
  }


  def existsByDirectoryIdAndName(directoryId: Long, name: String): Boolean = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM page WHERE directoryId = $directoryId AND name = $name".as(scalar[Long].single) > 0
    }
  }
}
