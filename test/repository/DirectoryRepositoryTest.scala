package repository

import anorm.SqlParser.scalar
import anorm._
import models._
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.db.Database
import play.api.test.Injecting

class DirectoryRepositoryTest extends PlaySpec with GuiceOneServerPerSuite with Injecting with BeforeAndAfterEach {

  val db = inject[Database]
  val directoryRepository = inject[DirectoryRepository]

  val directory1 = Directory(1, "app", "directory1", "description1", 0, "", "project_id1>branch1>", 1, Seq(), Seq())
  val directory2 = Directory(2, "front", "directory2", "description2", 1, "", "project_id1>branch1>", 1, Seq(), Seq())
  val directory3 = Directory(3, "back", "directory3", "description3", 2, "", "project_id2>branch1>", 3, Seq(), Seq())
  val directories = Seq(directory1, directory2, directory3)

  override def beforeEach(): Unit = {
    db.withConnection { implicit connection =>
      directories.foreach { directory =>
        SQL"""INSERT INTO directory (id, name, label, description, `order`, relativePath, path, branchId)
           VALUES (${directory.id},${directory.name},${directory.label},${directory.description},${directory.order},${directory.relativePath},${directory.path},${directory.branchId})"""
          .executeInsert()
      }
    }
  }

  override def afterEach(): Unit = {
    db.withConnection { implicit connection =>
      SQL"TRUNCATE TABLE directory".executeUpdate()
      SQL"ALTER TABLE directory ALTER COLUMN id RESTART WITH 1".executeUpdate()
      ()
    }
  }

  "DirectoryRepository" should {
    "count the number of directories" in {
      directoryRepository.count() mustBe 3
    }

    "delete all directories" in {
      directoryRepository.deleteAll()
      db.withConnection { implicit connection =>
        SQL"SELECT COUNT(*) FROM directory".as(scalar[Long].single) mustBe 0
      }
    }

    "delete a directory" in {
      directoryRepository.delete(directory1)
    }

    "delete a directory by id" in {
      directoryRepository.deleteById(directory1.id)
      db.withConnection { implicit connection =>
        SQL"SELECT COUNT(*) FROM directory WHERE id = ${directory1.id}".as(scalar[Long].single) mustBe 0
      }
    }

    "find all by id" in {
      directoryRepository.findAllById(directories.tail.map(_.id)) must contain theSameElementsAs directories.tail
    }

    "find a directory by id" in {
      directoryRepository.findById(directory1.id) mustBe Some(directory1)
    }

    "get all directories" in {
      directoryRepository.findAll() must contain theSameElementsAs directories
    }

    "get all directories by projectId" in {
      directoryRepository.findAllByBranchId(1) must contain theSameElementsAs Seq(directory1, directory2)
    }

    "check if a directory exist by id" in {
      directoryRepository.existsById(directory1.id) mustBe true
    }

    "save a directory" in {
      val newDirectory = Directory(-1, "assets", "directory1", "description4", 3, "", "project_id1>branch1>", 1, Seq(), Seq())
      directoryRepository.save(newDirectory) mustBe newDirectory.copy(id = 4)
    }

    "update a directory" in {
      val updatedDirectory = directory1.copy(description = "rien de rien")
      directoryRepository.save(updatedDirectory) mustBe updatedDirectory
      directoryRepository.findAll() must contain theSameElementsAs Seq(updatedDirectory, directory2, directory3)
    }

    "save all directories by projectId" in {
      val directory4 = Directory(-1, "conf", "directory4", "description4", 4, "", "project_id2>branch1>", 3, Seq(), Seq())
      val directory5 = Directory(-1, "test", "directory5", "description5", 5, "", "project_id3>branch1>", 3, Seq(), Seq())
      val expectedDirectories = Seq(directory4.copy(id = 4), directory5.copy(id = 5))

      directoryRepository.saveAll(Seq(directory4, directory5)) must contain theSameElementsAs expectedDirectories
      directoryRepository.findAll() must contain theSameElementsAs directories ++ expectedDirectories
    }
  }

}
