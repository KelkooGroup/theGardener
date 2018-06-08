package services

import java.io.File
import java.util.{List => JList}

import gherkin.ast.GherkinDocument
import gherkin.{AstBuilder, Parser, ast}
import models._

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.io.Source

class ProjectService {

  val projects = mutable.Map[String, Project]()

  def parseFeatureFile(projectId: String, filePath: String): Feature = {
    val featureFile = new File(filePath)

    var branch = filePath.substring(filePath.indexOf(projectId + "/") + projectId.length + 1)
    branch = branch.substring(0, branch.indexOf("/"))

    val id = s"$projectId/$branch/${featureFile.getName}"

    val parser = new Parser[GherkinDocument](new AstBuilder())
    val gherkinDocument = parser.parse(Source.fromFile(featureFile).mkString)

    val comments = gherkinDocument.getComments.asScala.map(_.getText)
    val feature = gherkinDocument.getFeature
    val (tags, _, _, _) = mapGherkinTags(feature.getTags)


    val scenarios = feature.getChildren.asScala.zipWithIndex.flatMap { case (background: ast.Background, id: Int) => Some(Background(id, background.getKeyword, background.getName, trim(background.getDescription), mapGherkinSteps(background.getSteps)))
    case (scenario: ast.Scenario, id: Int) =>

      val (tags, abstractionLevel, caseType, workflowStep) = mapGherkinTags(scenario.getTags)

      Some(Scenario(id, tags, abstractionLevel, caseType, workflowStep, scenario.getKeyword, scenario.getName, trim(scenario.getDescription), mapGherkinSteps(scenario.getSteps)))
    case (scenario: ast.ScenarioOutline, id: Int) => val (tags, abstractionLevel, caseType, workflowStep) = mapGherkinTags(scenario.getTags)

      Some(ScenarioOutline(id, tags, abstractionLevel, caseType, workflowStep, scenario.getKeyword, scenario.getName, trim(scenario.getDescription), mapGherkinSteps(scenario.getSteps), mapGherkinExamples(scenario.getExamples)))
    case _ => None
    }

    Feature(id, branch, tags, feature.getLanguage, feature.getKeyword, feature.getName, trim(feature.getDescription), scenarios, comments)
  }

  private def mapGherkinSteps(gherkinSteps: JList[ast.Step]): Seq[Step] = {
    gherkinSteps.asScala.zipWithIndex.map { case (step, id) => val argument = step.getArgument match {
      case dataTable: ast.DataTable => dataTable.getRows.asScala.map(_.getCells.asScala.map(_.getValue))
      case tableRow: ast.TableRow => Seq(tableRow.getCells.asScala.map(_.getValue))
      case _ => Seq()
    }

      Step(id, step.getKeyword.trim, step.getText, argument)
    }
  }

  private def mapGherkinTags(gherkinTags: JList[ast.Tag]): (Seq[String], String, String, String) = {
    val tags = gherkinTags.asScala.map(_.getName.replace("@", ""))
    val cleanTags = tags.map(_.replace("_", "")).map(_.toLowerCase).toSet

    val abstractionLevel = Feature.abstractionLevels.flatMap(level => cleanTags.map(tag => (level._1, tag) -> level._2)).find(level => level._2.exists(level._1._2.startsWith)).map(_._1._1).getOrElse("level_1_specification")

    val caseType = Feature.caseTypes.find(caseType => tags.exists(_.startsWith(caseType._2))).map(_._1).getOrElse("nominal_case")
    val workflowStep = Feature.workflowSteps.intersect(cleanTags).headOption.getOrElse("valid")

    (tags, abstractionLevel, caseType, workflowStep)
  }

  private def mapGherkinExamples(gherkinExamples: JList[ast.Examples]): Seq[Examples] = {
    gherkinExamples.asScala.zipWithIndex.map { case (examples, id) =>

      val (tags, _, _, _) = mapGherkinTags(examples.getTags)
      val tableHeader = examples.getTableHeader.getCells.asScala.map(_.getValue)
      val tableBody = examples.getTableBody.asScala.map(_.getCells.asScala.map(_.getValue))

      Examples(id, tags, examples.getKeyword, trim(examples.getDescription), tableHeader, tableBody)
    }
  }

  private def trim(s: String) = Option(s).map(_.trim).getOrElse("")
}
