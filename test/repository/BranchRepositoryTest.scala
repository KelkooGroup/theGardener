package repository

import anorm.SqlParser.scalar
import anorm._
import models._
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.db.Database
import play.api.test.Injecting

class BranchRepositoryTest extends PlaySpec with GuiceOneServerPerSuite with Injecting with BeforeAndAfterEach {

  val db = inject[Database]
  val branchRepository = inject[BranchRepository]

  val branch1 = Branch(1, "name", true, "id1")
  val branch2 = Branch(2, "name2", true, "id1")
  val branch3 = Branch(3, "name3", false, "id2")
  val branches = Seq(branch1, branch2, branch3)

  val project1 = Project("id1", "name1", "repositoryUrl1", "stableBranch1", "featuresRootPath1")
  val project2 = Project("id2", "name2", "repositoryUrl2", "stableBranch2", "featuresRootPath2")
  val project3 = Project("id3", "name3", "repositoryUrl3", "stableBranch3", "featuresRootPath3")
  val projects = Seq(project1, project2, project3)

  override def beforeEach() {
    db.withConnection { implicit connection =>
      branches.foreach { branch =>
        SQL"""INSERT INTO branch (id, name, isStable, projectId)
           VALUES (${branch.id}, ${branch.name}, ${branch.isStable}, ${branch.projectId})"""
          .executeInsert()
      }
    }

    db.withConnection { implicit connection =>
      projects.foreach { project =>
        SQL"""INSERT INTO project (id, name, repositoryUrl, stableBranch,featuresRootPath)
           VALUES (${project.id}, ${project.name}, ${project.repositoryUrl},${project.stableBranch}, ${project.featuresRootPath})"""
          .executeInsert()
      }
    }
  }

  override def afterEach() {
    db.withConnection { implicit connection =>
      SQL"TRUNCATE TABLE branch".executeUpdate()
      SQL"TRUNCATE TABLE project".executeUpdate()
    }
  }

  "GetFeatureRepository" should {
    "count the number of branches" in {
      branchRepository.count() mustBe 3
    }

    "delete all branches" in {
      branchRepository.deleteAll()
      db.withConnection { implicit connection =>
        SQL"SELECT COUNT(*) FROM branch".as(scalar[Long].single) mustBe 0
      }
    }

    "delete a branch" in {
      branchRepository.delete(branch1)
    }

    "delete a branch by id" in {
      branchRepository.deleteById(branch1.id)
      db.withConnection { implicit connection =>
        SQL"SELECT COUNT(*) FROM branch WHERE id = ${branch1.id}".as(scalar[Long].single) mustBe 0
      }
    }

    "find all by id" in {
      branchRepository.findAllById(branches.tail.map(_.id)) must contain theSameElementsAs branches.tail
    }

    "find a branch by id" in {
      branchRepository.findById(branch1.id) mustBe Some(branch1)
    }

    "get all branches" in {
      branchRepository.findAll() must contain theSameElementsAs branches
    }

    "get all branches by projectId" in {
      branchRepository.findAllByProjectId("id1") must contain theSameElementsAs Seq(branch1, branch2)
    }

    "check if a branch exist by projectId" in {
      branchRepository.existsByProjectId(1, "id1") mustBe true
    }

    "check if a branch exist by id" in {
      branchRepository.existsById(branch1.id) mustBe true
    }

    "save a branch" in {
      val branch4 = Branch(1, "name6", false, "id1")
      branchRepository.save(branch4)
      branchRepository.findById(branch4.id) mustBe Some(branch4)
    }

    "save all branches by projectId" in {
      val newBranches = Seq(Branch(6, "name6", false, "id1"), Branch(7, "name7", true, "id1"))
      branchRepository.saveAll(newBranches)
      branchRepository.findAllByProjectId("id1") must contain theSameElementsAs (newBranches :+ branch1 :+ branch2)
    }
  }
}

