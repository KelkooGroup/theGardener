package repository


import anorm.SqlParser.scalar
import anorm._
import models._
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.db.Database
import play.api.test.Injecting

class HierarchyRepositoryTest extends PlaySpec with GuiceOneServerPerSuite with Injecting with BeforeAndAfterEach {
  val db = inject[Database]
  val hierarchyRepository = inject[HierarchyRepository]
  val hierarchyNode1 = HierarchyNode("id1", "slugName1", "name1", "childrenLabel1", "childLabel1")
  val hierarchyNode2 = HierarchyNode("id2", "slugName2", "name2", "childrenLabel2", "childLabel2")

  val hierarchyNodes = Seq(hierarchyNode1, hierarchyNode2)

  override def beforeEach(): Unit = {
    db.withConnection { implicit connection =>
      hierarchyNodes.foreach { hierarchyNode =>
        SQL"""INSERT INTO hierarchyNode (id, slugName, name, childrenLabel, childLabel)
             VALUES (${hierarchyNode.id}, ${hierarchyNode.slugName}, ${hierarchyNode.name}, ${hierarchyNode.childrenLabel}, ${hierarchyNode.childLabel})""".executeInsert()
      }
    }
  }

  override def afterEach() {
    db.withConnection { implicit connection =>
      SQL"TRUNCATE TABLE hierarchyNode".executeUpdate()
      SQL"TRUNCATE TABLE project_hierarchyNode".executeUpdate()
      SQL"TRUNCATE TABLE project".executeUpdate()
    }
  }

  "HierarchyRepository" should {
    "find all hierarchyNodes" in {
      hierarchyRepository.findAll() must contain theSameElementsAs hierarchyNodes
    }

    "find an hierarchyNode by id" in {
      hierarchyRepository.findById(hierarchyNode1.id) mustBe Some(hierarchyNode1)
    }

    "find an hierarchyNode by slugName" in {
      hierarchyRepository.findBySlugName(hierarchyNode1.slugName) mustBe Some(hierarchyNode1)
    }

    "delete all hierarchyNodes" in {
      hierarchyRepository.deleteAll()

      db.withConnection { implicit connection =>
        SQL"SELECT COUNT(*) FROM hierarchyNode".as(scalar[Long].single) mustBe 0
      }
    }

    "delete an hierarchyNode" in {
      hierarchyRepository.deleteById(hierarchyNode1.id)

      db.withConnection { implicit connection =>
        SQL"SELECT COUNT(*) FROM hierarchyNode WHERE id = ${hierarchyNode1.id}".as(scalar[Long].single) mustBe 0
      }
    }

    "check if an hierarchyNode exist by id" in {
      hierarchyRepository.existsById(hierarchyNode1.id) mustBe true
    }

    "save an hierarchyNode" in {
      val hierarchyNode3 = HierarchyNode("id3", "slugName3", "name3", "childrenLabel3", "childLabel3")
      hierarchyRepository.save(hierarchyNode3)
      hierarchyRepository.findById(hierarchyNode3.id) mustBe Some(hierarchyNode3)
    }

    "save all hierarchyNodes" in {
      val newHierarchyNodes = Seq(HierarchyNode("id4", "slugName4", "name4", "childrenLabel4", "childLabel4"), HierarchyNode("id5", "slugName5", "name5", "childrenLabel5", "childLabel5"))
      hierarchyRepository.saveAll(newHierarchyNodes)
      hierarchyRepository.findAll() must contain theSameElementsAs (newHierarchyNodes :+ hierarchyNode1 :+ hierarchyNode2)
    }

    "find hierarchyNodes by projectId" in {
      db.withConnection { implicit connection =>
        SQL"INSERT INTO project (id, name, repositoryUrl, stableBranch,featuresRootPath,documentationRootPath) VALUES ('id1', 'name1', 'repositoryUrl1', 'stableBranch1', 'featuresRootPath1', '/doc')".executeInsert()
        SQL"INSERT INTO project_hierarchyNode (projectId, hierarchyId) VALUES ('id1', 'id1')".executeInsert()
        SQL"INSERT INTO project_hierarchyNode (projectId, hierarchyId) VALUES ('id1', 'id2')".executeInsert()
      }
      hierarchyRepository.findAllByProjectId("id1") must contain theSameElementsAs Seq(hierarchyNode1, hierarchyNode2)
    }
  }
}

