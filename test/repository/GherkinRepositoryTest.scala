package repository

import anorm._
import controllers.dto.ProjectDocumentationDTO
import models._
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.db.Database
import play.api.test.Injecting

class GherkinRepositoryTest extends PlaySpec with GuiceOneServerPerSuite with Injecting with BeforeAndAfterEach {

  val db = inject[Database]

  val projectRepository = inject[ProjectRepository]
  val branchRepository = inject[BranchRepository]
  val featureRepository = inject[FeatureRepository]
  val scenarioRepository = inject[ScenarioRepository]

  val gherkinRepository = inject[GherkinRepository]

  val node = HierarchyNode(".01.", "eng", "Eng", "Systems", "System")
  val project1 = Project("project1", "Project 1", "repositoryUrl1", "branch2", "featuresRootPath1")
  val branch1 = Branch(1, "branch1", isStable = false, "project1")
  val branch2 = Branch(2, "branch2", isStable = true, "project1")
  val feature1 = Feature(1, 1, "path1", None, Seq(), Some("language1"), "keyword1", "feature1", "description1", Seq(), Seq("comments1"))
  val feature2 = Feature(2, 1, "path2", None, Seq(), Some("language2"), "keyword2", "feature2", "description2", Seq(), Seq("comments1"))
  val scenario1 = Scenario(1, Seq("s_tag1", "s_tag2"), "abstractionLevel1", "caseType1", "workflowStep1", "keyword1", "scenario1", "description1", Seq(Step(1, "keyword1", "text1", Seq(Seq("argument1")))))
  val scenario2 = Scenario(2, Seq("s_tag2", "s_tag3"), "abstractionLevel1", "caseType1", "workflowStep1", "keyword1", "scenario2", "description1", Seq(Step(2, "keyword1", "text1", Seq(Seq("argument1")))))
  val scenario3 = Scenario(3, Seq("s_tag1", "s_tag3"), "abstractionLevel1", "caseType1", "workflowStep1", "keyword1", "scenario3", "description1", Seq(Step(3, "keyword1", "text1", Seq(Seq("argument1")))))


  override def beforeEach() {
    projectRepository.saveAll(Seq(project1))
    branchRepository.saveAll(Seq(branch1, branch2))
    featureRepository.saveAll(Seq(feature1, feature2))
    scenarioRepository.saveAll(1, Seq(scenario1, scenario2))
    scenarioRepository.saveAll(2, Seq(scenario3))
  }

  override def afterEach() {
    db.withConnection { implicit connection =>
      SQL"TRUNCATE TABLE project".executeUpdate()
      SQL"TRUNCATE TABLE branch".executeUpdate()
      SQL"TRUNCATE TABLE feature".executeUpdate()
      SQL"TRUNCATE TABLE feature_tag".executeUpdate()
      SQL"TRUNCATE TABLE scenario".executeUpdate()
      SQL"TRUNCATE TABLE scenario_tag".executeUpdate()
      SQL"TRUNCATE TABLE tag".executeUpdate()
    }
  }

  "DocumentationRepository" should {

    "build documentation from " in {
      val documentation: ProjectDocumentationDTO = gherkinRepository.buildProjectGherkin(ProjectMenuItem(project1.id, project1.name, branch1.name))

      documentation.id mustBe project1.id
      documentation.name mustBe project1.name
      documentation.branches.size mustBe 1
      documentation.branches.head.id mustBe branch1.id
      documentation.branches.head.name mustBe branch1.name
      documentation.branches.head.isStable mustBe branch1.isStable
      documentation.branches.head.features.size mustBe 2

      documentation.branches.head.features.head.id mustBe feature1.id
      documentation.branches.head.features.head.path mustBe feature1.path
      documentation.branches.head.features.head.scenarios.size mustBe 2
      documentation.branches.head.features.head.scenarios.head.id mustBe scenario1.id
      documentation.branches.head.features.head.scenarios(1).id mustBe scenario2.id
      documentation.branches.head.features.head.scenarios.head.name mustBe scenario1.name
      documentation.branches.head.features.head.scenarios(1).name mustBe scenario2.name

      documentation.branches.head.features(1).id mustBe feature2.id
      documentation.branches.head.features(1).path mustBe feature2.path
      documentation.branches.head.features(1).scenarios.size mustBe 1
      documentation.branches.head.features(1).scenarios.head.id mustBe scenario3.id
      documentation.branches.head.features(1).scenarios.head.name mustBe scenario3.name


      documentation.branches.head.features.head.tags mustBe Seq()
      documentation.branches.head.features(1).tags mustBe Seq()
      documentation.branches.head.features.head.scenarios.head.asInstanceOf[Scenario].tags mustBe Seq("s_tag1", "s_tag2")
      documentation.branches.head.features.head.scenarios(1).asInstanceOf[Scenario].tags mustBe Seq("s_tag2", "s_tag3")
      documentation.branches.head.features(1).scenarios.head.asInstanceOf[Scenario].tags mustBe Seq("s_tag1", "s_tag3")
    }
  }
}
