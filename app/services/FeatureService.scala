package services

import java.io.{File, FileReader}
import java.util.{List => JList}

import com.typesafe.config.Config
import gherkin.ast.GherkinDocument
import gherkin.{AstBuilder, Parser, ast}
import javax.inject._
import models._
import repositories.FeatureRepository
import resource._
import utils._

import scala.jdk.CollectionConverters._
import scala.util.Try


class FeatureService @Inject()(config: Config, featureRepository: FeatureRepository) {
  val projectsRootDirectory = config.getString("projects.root.directory")

  def getLocalRepository(projectId: String, branch: String): String = s"$projectsRootDirectory$projectId/$branch/".fixPathSeparator

  def parseBranchDirectory(project: Project, branch: Branch, directoryPath: String): Seq[Feature] = {
    val dir = new File(directoryPath)
    if (dir.exists()) {
      dir.listFiles().toSeq.flatMap {
        case d if d.isDirectory => parseBranchDirectory(project, branch, d.getPath)
        case f if f.isFile && f.getName.endsWith(".feature") => parseFeatureFile(project.id, branch, f.getPath).toOption
        case _ => None
      }
    }
    else {
      Seq()
    }

  }


  def parseFeatureFile(projectId: String, branch: Branch, filePath: String): Try[Feature] = {
    Try {
      val featureFile = new File(filePath)

      val relativeFilePath = filePath.replace(getLocalRepository(projectId, branch.name), "")

      val featureId = featureRepository.findByBranchIdAndPath(branch.id, relativeFilePath).map(_.id).getOrElse(0L)
      val parser = new Parser[GherkinDocument](new AstBuilder())

      managed(new FileReader(featureFile)).acquireAndGet {
        featureReader =>
          val gherkinDocument = parser.parse(featureReader)

          val comments = gherkinDocument.getComments.asScala.toSeq.map(_.getText)
          val feature = gherkinDocument.getFeature

          val (tags, _, _, _) = mapGherkinTags(feature.getTags)
          var backgroundOption: Option[Background] = None

          val scenarios = feature.getChildren.asScala.toSeq.flatMap {

            case background: ast.Background =>
              backgroundOption = Some(Background(0, background.getKeyword, background.getName, trim(background.getDescription), mapGherkinSteps(background.getSteps)))
              backgroundOption

            case scenario: ast.Scenario =>

              val (tags, abstractionLevel, caseType, workflowStep) = mapGherkinTags(scenario.getTags)

              Some(Scenario(0, tags, abstractionLevel, caseType, workflowStep, scenario.getKeyword, scenario.getName, trim(scenario.getDescription), mapGherkinSteps(scenario.getSteps)))


            case scenario: ast.ScenarioOutline =>
              val (tags, abstractionLevel, caseType, workflowStep) = mapGherkinTags(scenario.getTags)

              Some(ScenarioOutline(0, tags, abstractionLevel, caseType, workflowStep, scenario.getKeyword, scenario.getName, trim(scenario.getDescription), mapGherkinSteps(scenario.getSteps), mapGherkinExamples(scenario.getExamples)))

            case _ => None
          }

          Feature(featureId, branch.id, relativeFilePath, backgroundOption, tags, Option(feature.getLanguage), feature.getKeyword, feature.getName, trim(feature.getDescription), scenarios, comments)
      }
    }.logError(s"Error while parsing file $filePath")
  }

  private def mapGherkinSteps(gherkinSteps: JList[ast.Step]): Seq[Step] = {
    gherkinSteps.asScala.toSeq.zipWithIndex.map {
      case (step, id) =>
        val (argument, argumentTextType) = step.getArgument match {
          case dataTable: ast.DataTable => (dataTable.getRows.asScala.toSeq.map(_.getCells.asScala.toSeq.map(_.getValue)), None)
          case tableRow: ast.TableRow => (Seq(tableRow.getCells.asScala.toSeq.map(_.getValue)), None)
          case docString: ast.DocString => (Seq(Seq(docString.getContent)), Some(docString.getContentType))
          case _ => (Seq(), None)
        }

        Step(id.toLong, step.getKeyword.trim, step.getText, argument, argumentTextType)
    }
  }

  private def mapGherkinTags(gherkinTags: JList[ast.Tag]): (Seq[String], String, String, String) = {
    val tags = gherkinTags.asScala.toSeq.map(_.getName.replace("@", ""))
    val cleanTags = tags.map(_.replace("_", "")).map(_.toLowerCase).toSet

    val abstractionLevel = Feature.abstractionLevels.flatMap(level => cleanTags.map(tag => (level._1, tag) -> level._2))
      .find(level => level._2.exists(level._1._2.startsWith))
      .map(_._1._1)
      .getOrElse("level_1_specification")

    val caseType = Feature.caseTypes.find(caseType => tags.exists(_.startsWith(caseType._2))).map(_._1).getOrElse("nominal_case")
    val workflowStep = Feature.workflowSteps.intersect(cleanTags).headOption.getOrElse("valid")

    (tags, abstractionLevel, caseType, workflowStep)
  }

  private def mapGherkinExamples(gherkinExamples: JList[ast.Examples]): Seq[Examples] = {
    gherkinExamples.asScala.toSeq.zipWithIndex.map {
      case (examples, id) =>

        val (tags, _, _, _) = mapGherkinTags(examples.getTags)
        val tableHeader = examples.getTableHeader.getCells.asScala.toSeq.map(_.getValue)
        val tableBody = examples.getTableBody.asScala.toSeq.map(_.getCells.asScala.toSeq.map(_.getValue))

        Examples(id.toLong, tags, examples.getKeyword, trim(examples.getDescription), tableHeader, tableBody)
    }
  }

  private def trim(s: String) = Option(s).map(_.trim).getOrElse("")
}
