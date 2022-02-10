package services

import com.typesafe.config.Config
import io.cucumber.gherkin.{GherkinDocumentBuilder, Parser}
import io.cucumber.messages.{IdGenerator, types}
import io.cucumber.messages.types.GherkinDocument
import models._
import repositories.FeatureRepository
import resource._
import utils._

import java.io.{File, FileReader}
import java.util.{List => JList}
import javax.inject._
import scala.jdk.CollectionConverters._
import scala.util.Try

class IncrementingIdGenerator extends IdGenerator {
  private var next = 0

  override def newId: String = {
    next +=1
    Integer.toString(next)
  }
}

class FeatureService @Inject()(config: Config, featureRepository: FeatureRepository) {
  private val projectsRootDirectory = config.getString("projects.root.directory")
  private val idGenerator= new IncrementingIdGenerator()

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
      val parser = new Parser[GherkinDocument](new GherkinDocumentBuilder(idGenerator))

      managed(new FileReader(featureFile)).acquireAndGet {
        featureReader =>
          val gherkinDocument = parser.parse(featureReader)

          val comments = gherkinDocument.getComments.asScala.toSeq.map(_.getText)
          val feature = gherkinDocument.getFeature

          val (tags, _, _, _) = mapGherkinTags(feature.getTags)
          var backgroundOption: Option[Background] = None

          val scenarios = feature.getChildren.asScala.toSeq.flatMap { child =>

            val background = Option(child.getBackground)
            val scenario = Option(child.getScenario)

            (background, scenario) match {


              case (Some(background), _) =>
                backgroundOption = Some(Background(0, background.getKeyword, background.getName, trim(background.getDescription), mapGherkinSteps(background.getSteps)))
                backgroundOption

              case (_, Some(scenario)) =>

                val (tags, abstractionLevel, caseType, workflowStep) = mapGherkinTags(scenario.getTags)

                if (scenario.getExamples.isEmpty) {
                  Some(Scenario(0, tags, abstractionLevel, caseType, workflowStep, scenario.getKeyword, scenario.getName, trim(scenario.getDescription), mapGherkinSteps(scenario.getSteps)))
                } else {
                  Some(ScenarioOutline(0, tags, abstractionLevel, caseType, workflowStep, scenario.getKeyword, scenario.getName, trim(scenario.getDescription), mapGherkinSteps(scenario.getSteps), mapGherkinExamples(scenario.getExamples)))
                }

              case _ => None
            }
          }

          Feature(featureId, branch.id, relativeFilePath, backgroundOption, tags, Option(feature.getLanguage), feature.getKeyword, feature.getName, trim(feature.getDescription), scenarios, comments)
      }
    }.logError(s"Error while parsing file $filePath")
  }

  private def mapGherkinSteps(gherkinSteps: JList[types.Step]) = {
    gherkinSteps.asScala.toSeq.zipWithIndex.map {
      case (step, id) =>

        val datatable = Option(step.getDataTable)
        val docString = Option(step.getDocString)

        val (argument, argumentTextType) = (datatable, docString) match {
          case (Some(dataTable), _) => (dataTable.getRows.asScala.toSeq.map(_.getCells.asScala.toSeq.map(_.getValue)), None)
          case (_, Some(docString)) => (Seq(Seq(docString.getContent)), Some(docString.getMediaType))
          case _ => (Seq(), None)
        }

        Step(id.toLong, step.getKeyword.trim, step.getText, argument, argumentTextType)
    }
  }

  private def mapGherkinTags(gherkinTags: JList[types.Tag]) = {
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

  private def mapGherkinExamples(gherkinExamples: JList[types.Examples]) = {
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
