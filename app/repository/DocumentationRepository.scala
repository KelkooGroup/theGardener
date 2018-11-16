package repository

import anorm.SqlParser.{get, _}
import anorm._
import controllers.{BranchDocumentationDTO, Documentation, ProjectDocumentationDTO}
import javax.inject.Inject
import models._
import play.api.db.Database
import play.api.libs.json.Json
import models.Feature.{backgroundFormat, examplesFormat, stepFormat}

case class ProjectCriteria(projectId: String, branchName: String)

case class DocumentationRowJoin(
                                 branch_id: Long, branch_name: String, branch_isStable: Boolean, feature_id: Long, feature_path: String, feature_backgroundAsJson: Option[String], feature_language: Option[String],
                                 feature_keyword: String, feature_name: String, feature_description: String, feature_comments: String,
                                 scenario_id: Long, scenario_abstractionLevel: String, scenario_caseType: String, scenario_workflowStep: String, scenario_keyword: String,
                                 scenario_name: String, scenario_description: String, scenario_stepsAsJson: String, scenario_examplesAsJson: Option[String]
                               )

case class DocumentationTagRowJoin(
                                    branch_id: Long, feature_id: Long, feature_tag_name: String,
                                    scenario_id: Long, scenario_tag_name: String
                                  )

class DocumentationRepository @Inject()(db: Database) {

  private val parserDocumentationRowJoin = for {
    branch_id <- long("branch_id")
    branch_name <- str("branch_name")
    branch_isStable <- bool("branch_isStable")
    feature_id <- long("feature_id")
    feature_path <- str("feature_path")
    feature_backgroundAsJson <- get[Option[String]]("feature_backgroundAsJson")
    feature_language <- get[Option[String]]("feature_language")
    feature_keyword <- str("feature_keyword")
    feature_name <- str("feature_name")
    feature_description <- str("feature_description")
    feature_comments <- str("feature_comments")
    scenario_id <- long("scenario_id")
    scenario_abstractionLevel <- str("scenario_abstractionLevel")
    scenario_caseType <- str("scenario_caseType")
    scenario_workflowStep <- str("scenario_workflowStep")
    scenario_keyword <- str("scenario_keyword")
    scenario_name <- str("scenario_name")
    scenario_description <- str("scenario_description")
    scenario_stepsAsJson <- str("scenario_stepsAsJson")
    scenario_examplesAsJson <- get[Option[String]]("scenario_examplesAsJson")
  } yield DocumentationRowJoin(branch_id, branch_name, branch_isStable, feature_id, feature_path, feature_backgroundAsJson, feature_language,
    feature_keyword, feature_name, feature_description, feature_comments,
    scenario_id, scenario_abstractionLevel, scenario_caseType, scenario_workflowStep, scenario_keyword,
    scenario_name, scenario_description, scenario_stepsAsJson, scenario_examplesAsJson)

  private val parserDocumentationTagRowJoin = for {
    branch_id <- long("branch_id")
    feature_id <- long("feature_id")
    feature_tag_name <- str("feature_tag_name")
    scenario_id <- long("scenario_id")
    scenario_tag_name <- str("scenario_tag_name")
  } yield DocumentationTagRowJoin(branch_id, feature_id, feature_tag_name, scenario_id, scenario_tag_name)


  def buidProjectDocumentation(projectCriteria: ProjectCriteria): ProjectDocumentationDTO = {

    val tagsRows: Seq[DocumentationTagRowJoin] = db.withConnection { implicit connection =>
      SQL"""  select b.id as   branch_id,
                f.id as   feature_id,
                s.id as   scenario_id,
                ft.name as feature_tag_name,
                st.name as scenario_tag_name
                from branch   b
                join feature  f      on f.branchId  = b.id
                join feature_tag ft  on ft.featureId = f.id
                join scenario s      on s.featureId = f.id
                join scenario_tag st  on st.scenarioId = s.id
            where b.projectId = ${projectCriteria.projectId}  and b.name = ${projectCriteria.branchName}
            order by f.id, s.id;

         """.as(parserDocumentationTagRowJoin.*)
    }

    val mapFeatureTags = tagsRows.groupBy(r => r.feature_id).foldLeft(Map[Long, Set[String]]()) { (mapFeatureTags, tagsRowsByFeature) =>
      val tags = tagsRowsByFeature._2.foldLeft(Set[String]())((l, e) => l + e.feature_tag_name)
      mapFeatureTags + (tagsRowsByFeature._1 -> tags)
    }

    val mapScenarioTags = tagsRows.groupBy(r => r.scenario_id).foldLeft(Map[Long, Set[String]]()) { (mapScenarioTags, tagsRowsByScenario) =>
      val tags = tagsRowsByScenario._2.foldLeft(Set[String]())((l, e) => l + e.scenario_tag_name)
      mapScenarioTags + (tagsRowsByScenario._1 -> tags)
    }

    val rows: Seq[DocumentationRowJoin] = db.withConnection { implicit connection =>
      SQL"""  select b.id as branch_id,
                   b.name as branch_name,
                   b.isStable as branch_isStable,
                   f.id as   feature_id,
                   f.path as   feature_path,
                   f.backgroundAsJson as   feature_backgroundAsJson,
                   f.language as   feature_language,
                   f.keyword as   feature_keyword,
                   f.name as   feature_name,
                   f.description as   feature_description,
                   f.comments as   feature_comments,
                   s.id as   scenario_id,
                   s.abstractionLevel as   scenario_abstractionLevel,
                   s.caseType as   scenario_caseType,
                   s.workflowStep as   scenario_workflowStep,
                   s.keyword as   scenario_keyword,
                   s.name as   scenario_name,
                   s.description as   scenario_description,
                   s.stepsAsJson as   scenario_stepsAsJson,
                   s.examplesAsJson as   scenario_examplesAsJson
            from branch   b
              join feature  f      on f.branchId  = b.id
              join scenario s      on s.featureId = f.id
            where b.projectId = ${projectCriteria.projectId}  and b.name = ${projectCriteria.branchName}
            order by f.id, s.id;

         """.as(parserDocumentationRowJoin.*)
    }

    val branch = Branch(rows.head.branch_id, rows.head.branch_name, rows.head.branch_isStable, projectCriteria.projectId)
    val features = rows.groupBy(r => r.feature_id).foldLeft(List[Feature]()) { (features, rowByFeature) =>

      val featureId = rowByFeature._1
      val scenarioRows = rowByFeature._2
      val featureRow = rowByFeature._2.head

      val scenarios = scenarioRows.groupBy(r => r.scenario_id).foldLeft(List[ScenarioDefinition]()) { (scenarios, rowByScenario) =>

        val scenarioId = rowByScenario._1
        val stepsRows = rowByScenario._2
        val scenarioRow = rowByScenario._2.head
        val scenarioTags = mapScenarioTags(scenarioId)

        val scenario: ScenarioDefinition = if (scenarioRow.scenario_examplesAsJson.isEmpty) {
          Scenario(scenarioRow.scenario_id, scenarioTags.toSeq, scenarioRow.scenario_abstractionLevel, scenarioRow.scenario_caseType, scenarioRow.scenario_workflowStep, scenarioRow.scenario_keyword,
            scenarioRow.scenario_name, scenarioRow.scenario_description, Json.parse(scenarioRow.scenario_stepsAsJson).as[Seq[Step]])
        } else {
          ScenarioOutline(scenarioRow.scenario_id, scenarioTags.toSeq, scenarioRow.scenario_abstractionLevel, scenarioRow.scenario_caseType, scenarioRow.scenario_workflowStep, scenarioRow.scenario_keyword,
            scenarioRow.scenario_name, scenarioRow.scenario_description, Json.parse(scenarioRow.scenario_stepsAsJson).as[Seq[Step]], scenarioRow.scenario_examplesAsJson.map(Json.parse(_).as[Seq[Examples]]).getOrElse(Seq()))
        }
        scenario :: scenarios
      }
      val featureTags = mapFeatureTags(featureId)
      Feature(featureId, featureRow.branch_id, featureRow.feature_path,
        featureRow.feature_backgroundAsJson.map(Json.parse(_).as[Background]), featureTags.toSeq, featureRow.feature_language, featureRow.feature_keyword,
        featureRow.feature_name, featureRow.feature_description, scenarios) :: features
    }

    val branchDocumentation = BranchDocumentationDTO(branch, features)

    ProjectDocumentationDTO(projectCriteria.projectId, projectCriteria.branchName, Seq(branchDocumentation))
  }


}
