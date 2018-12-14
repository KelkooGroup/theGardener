package repository

import anorm.SqlParser.{get, _}
import anorm._
import controllers.{BranchDocumentationDTO, ProjectDocumentationDTO}
import javax.inject.Inject
import models.Feature.{backgroundFormat, examplesFormat, stepFormat}
import models._
import play.api.db.Database
import play.api.libs.json.Json
import scala.language.postfixOps

case class ProjectCriteria(projectId: String, projectName: String, branchName: String, mayFeatureFilter: Option[String] = None, mayTagsFilter: Option[Seq[String]] = None)

case class DocumentationRowJoin(branchId: Long, branchName: String, branchIsStable: Boolean, featureId: Long, featurePath: String, featureBackgroundAsJson: Option[String], featureLanguage: Option[String],
                                featureKeyword: String, featureName: String, featureDescription: String, featureComments: String,
                                scenarioId: Long, scenarioAbstractionLevel: String, scenarioCaseType: String, scenarioWorkflowStep: String, scenarioKeyword: String,
                                scenarioName: String, scenarioDescription: String, scenarioStepsAsJson: String, scenarioExamplesAsJson: Option[String])

case class DocumentationTagRowJoin(branchId: Long, featureId: Long, featureTagName: Option[String], scenarioId: Option[Long], scenarioTagName: Option[String])

@SuppressWarnings(Array("TraversableHead"))
class DocumentationRepository @Inject()(db: Database) {

  private val parserDocumentationRowJoin = for {
    branchId <- long("branch_id")
    branchName <- str("branch_name")
    branchIsStable <- bool("branch_isStable")
    featureId <- long("feature_id")
    featurePath <- str("feature_path")
    featureBackgroundAsJson <- get[Option[String]]("feature_backgroundAsJson")
    featureLanguage <- get[Option[String]]("feature_language")
    featureKeyword <- str("feature_keyword")
    featureName <- str("feature_name")
    featureDescription <- str("feature_description")
    featureComments <- str("feature_comments")
    scenarioId <- long("scenario_id")
    scenarioAbstractionLevel <- str("scenario_abstractionLevel")
    scenarioCaseType <- str("scenario_caseType")
    scenarioWorkflowStep <- str("scenario_workflowStep")
    scenarioKeyword <- str("scenario_keyword")
    scenarioName <- str("scenario_name")
    scenarioDescription <- str("scenario_description")
    scenarioStepsAsJson <- str("scenario_stepsAsJson")
    scenarioExamplesAsJson <- get[Option[String]]("scenario_examplesAsJson")
  } yield DocumentationRowJoin(branchId, branchName, branchIsStable, featureId, featurePath, featureBackgroundAsJson, featureLanguage,
    featureKeyword, featureName, featureDescription, featureComments,
    scenarioId, scenarioAbstractionLevel, scenarioCaseType, scenarioWorkflowStep, scenarioKeyword,
    scenarioName, scenarioDescription, scenarioStepsAsJson, scenarioExamplesAsJson)

  private val parserDocumentationTagRowJoin = for {
    branchId <- long("branch_id")
    featureId <- long("feature_id")
    featureTagName <- get[Option[String]]("feature_tag_name")
    scenarioId <- get[Option[Long]]("scenario_id")
    scenarioTagName <- get[Option[String]]("scenario_tag_name")
  } yield DocumentationTagRowJoin(branchId, featureId, featureTagName, scenarioId, scenarioTagName)


  def buildProjectDocumentation(projectCriteria: ProjectCriteria): ProjectDocumentationDTO = {

    val tagsRows: Seq[DocumentationTagRowJoin] = db.withConnection { implicit connection =>
      SQL"""  SELECT b.id AS   branch_id,
                f.id AS   feature_id,
                s.id AS   scenario_id,
                ft.name AS feature_tag_name,
                st.name AS scenario_tag_name
                FROM branch   b
                JOIN feature  f      ON f.branchId  = b.id
                left outer JOIN feature_tag ft  ON ft.featureId = f.id
                left outer JOIN scenario s      ON s.featureId = f.id
                left outer JOIN scenario_tag st  ON st.scenarioId = s.id
            WHERE b.projectId = ${projectCriteria.projectId}  AND b.name = ${projectCriteria.branchName}
            ORDER BY f.id, s.id;

         """.as(parserDocumentationTagRowJoin.*)
    }

    val mapFeatureTags = tagsRows.groupBy(r => r.featureId).foldLeft(Map[Long, Set[String]]()) { (mapFeatureTags, tagsRowsByFeature) =>
      val tags = tagsRowsByFeature._2.foldLeft(Set[String]())((l, e) => l + e.featureTagName.getOrElse("")).filter(_.nonEmpty)
      mapFeatureTags + (tagsRowsByFeature._1 -> tags)
    }

    val mapScenarioTags = tagsRows.groupBy(r => r.scenarioId).foldLeft(Map[Long, Set[String]]()) { (mapScenarioTags, tagsRowsByScenario) =>
      tagsRowsByScenario._1.map { scenarioId =>
        val tags = tagsRowsByScenario._2.foldLeft(Set[String]())((l, e) => l + e.scenarioTagName.getOrElse("")).filter(_.nonEmpty)
        mapScenarioTags + (scenarioId -> tags)

      }.getOrElse(mapScenarioTags)
    }

    val rows: Seq[DocumentationRowJoin] = db.withConnection { implicit connection =>

      val queryBuilder = new StringBuilder(
        s"""
        SELECT b.id AS branch_id,
                   b.name AS branch_name,
                   b.isStable AS branch_isStable,
                   f.id AS   feature_id,
                   f.path AS   feature_path,
                   f.backgroundAsJson AS   feature_backgroundAsJson,
                   f.language AS   feature_language,
                   f.keyword AS   feature_keyword,
                   f.name AS   feature_name,
                   f.description AS   feature_description,
                   f.comments AS   feature_comments,
                   s.id AS   scenario_id,
                   s.abstractionLevel AS   scenario_abstractionLevel,
                   s.caseType AS   scenario_caseType,
                   s.workflowStep AS   scenario_workflowStep,
                   s.keyword AS   scenario_keyword,
                   s.name AS   scenario_name,
                   s.description AS   scenario_description,
                   s.stepsAsJson AS   scenario_stepsAsJson,
                   s.examplesAsJson AS   scenario_examplesAsJson
            FROM branch   b
              JOIN feature  f      ON f.branchId  = b.id
              JOIN scenario s      ON s.featureId = f.id
            WHERE b.projectId = '${projectCriteria.projectId}'  AND b.name = '${projectCriteria.branchName}'
        """)

      if (projectCriteria.mayFeatureFilter.isDefined) {
        queryBuilder.append(s" AND f.path like '%${projectCriteria.mayFeatureFilter.get}%'")
      }
      queryBuilder.append(" ORDER BY f.id, s.id;")
      val query = queryBuilder.toString()
      SQL(query).as(parserDocumentationRowJoin *)
    }

    val branchDocumentation = if (rows.nonEmpty) {
      val branch = Branch(rows.head.branchId, rows.head.branchName, rows.head.branchIsStable, projectCriteria.projectId)
      val features = rows.groupBy(r => r.featureId).foldLeft(List[Feature]()) { (features, rowByFeature) =>

        val featureId = rowByFeature._1
        val scenarioRows = rowByFeature._2
        val featureRow = rowByFeature._2.head

        val scenarios = scenarioRows.groupBy(r => r.scenarioId).foldLeft(List[ScenarioDefinition]()) { (scenarios, rowByScenario) =>

          val scenarioId = rowByScenario._1
          val scenarioRow = rowByScenario._2.head
          val scenarioTags = mapScenarioTags.getOrElse(scenarioId, Set())

          val scenario: ScenarioDefinition = if (scenarioRow.scenarioExamplesAsJson.isEmpty) {
            Scenario(scenarioRow.scenarioId, scenarioTags.toSeq, scenarioRow.scenarioAbstractionLevel, scenarioRow.scenarioCaseType, scenarioRow.scenarioWorkflowStep, scenarioRow.scenarioKeyword,
              scenarioRow.scenarioName, scenarioRow.scenarioDescription, Json.parse(scenarioRow.scenarioStepsAsJson).as[Seq[Step]])
          } else {
            ScenarioOutline(scenarioRow.scenarioId, scenarioTags.toSeq, scenarioRow.scenarioAbstractionLevel, scenarioRow.scenarioCaseType, scenarioRow.scenarioWorkflowStep, scenarioRow.scenarioKeyword,
              scenarioRow.scenarioName, scenarioRow.scenarioDescription, Json.parse(scenarioRow.scenarioStepsAsJson).as[Seq[Step]], scenarioRow.scenarioExamplesAsJson.map(Json.parse(_).as[Seq[Examples]]).getOrElse(Seq()))
          }
          scenario :: scenarios
        }
        val featureTags = mapFeatureTags.getOrElse(featureId, Set())
        Feature(featureId, featureRow.branchId, featureRow.featurePath,
          featureRow.featureBackgroundAsJson.map(Json.parse(_).as[Background]), featureTags.toSeq, featureRow.featureLanguage, featureRow.featureKeyword,
          featureRow.featureName, featureRow.featureDescription, scenarios) :: features
      }

       Seq(BranchDocumentationDTO(branch, features))

    } else Seq()

    ProjectDocumentationDTO(projectCriteria.projectId, projectCriteria.projectName, branchDocumentation)
  }


}
