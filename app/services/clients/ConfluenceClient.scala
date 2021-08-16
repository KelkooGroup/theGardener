package services.clients

import play.api.Configuration
import play.api.libs.json.Json
import play.api.libs.ws.{WSAuthScheme, WSClient}

import javax.inject._
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal


case class ConfluenceAction(action: String)

case class ConfluenceMarkdownContent(markdown: String)

case class ConfluenceError(error: String, `type`: String = "ConfluenceError")

object ConfluenceError {
  val CONFLUENCE_ERROR = "ConfluenceError"
  val PROJECT_NOT_FOUND = "ProjectNotFound"
  val STABLE_BRANCH_NOT_FOUND = "StableBranchNotFound"
  val ROOT_DIRECTORY_NOT_FOUND = "RootDirectoryNotFound"
  val PROJECT_NOT_SETUP = "ProjectNotSetup"
}

case class ConfluencePageBodyEditor(value: String, representation: String = "editor")

case class ConfluencePageBody(editor: ConfluencePageBodyEditor)

case class ConfluencePageVersion(number: Int)

case class ConfluencePage(id: String, space: ConfluenceSpace, title: String, version: ConfluencePageVersion, body: ConfluencePageBody, `type`: String = "page", status: String = "current")

case class ConfluencePageTitle(id: String, title: String)

case class ConfluenceChildren(results: Seq[ConfluencePageTitle])

case class ConfluencePageChildren(page: ConfluenceChildren)

case class ConfluenceSpace(key: String)

case class ConfluencePageId(id: String)

case class ConfluencePageToCreate(space: ConfluenceSpace, title: String, version: ConfluencePageVersion, body: ConfluencePageBody, `type`: String = "page", ancestors: Seq[ConfluencePageId])

case class ConfluenceSearch(results: Seq[ConfluencePageTitle])

object ConfluencePage {
  implicit val formatConfluencePageBodyEditor = Json.format[ConfluencePageBodyEditor]
  implicit val formatConfluenceSpace = Json.format[ConfluenceSpace]
  implicit val formatConfluencePageBody = Json.format[ConfluencePageBody]
  implicit val formatConfluencePageVersion = Json.format[ConfluencePageVersion]
  implicit val formatConfluencePage = Json.format[ConfluencePage]
  implicit val formatConfluencePageTitle = Json.format[ConfluencePageTitle]
  implicit val formatConfluenceChildren = Json.format[ConfluenceChildren]
  implicit val formatConfluencePageChildren = Json.format[ConfluencePageChildren]
  implicit val formatConfluenceSearch = Json.format[ConfluenceSearch]
  implicit val formatConfluencePageId = Json.format[ConfluencePageId]
  implicit val formatConfluencePageToCreate = Json.format[ConfluencePageToCreate]
  implicit val formatConfluenceAction = Json.format[ConfluenceAction]

  def apply(): ConfluencePage
  = new ConfluencePage("", ConfluenceSpace(""), "", ConfluencePageVersion(1), ConfluencePageBody(ConfluencePageBodyEditor("")), "page", "current")
}

/// https://developer.atlassian.com/cloud/confluence/rest/api-group-content

class ConfluenceClient @Inject()(config: Configuration, wsClient: WSClient)(implicit ec: ExecutionContext) {

  import ConfluenceClient._
  import ConfluencePage._

  val user = config.get[String]("confluence.user")
  val password = config.get[String]("confluence.password")
  val confluenceRestUrl = config.get[String]("confluence.restApiUrl")

  val pageWithMarkdownTemplate =
    """<p class="auto-cursor-target"><br /></p><table class="wysiwyg-macro" data-macro-name="markdown" data-macro-parameters="allowHtml=true|atlassian-macro-output-type=INLINE|id=THE_GARDENER_MD" data-macro-schema-version="1" data-macro-body-type="PLAIN_TEXT"><tr><td class="wysiwyg-macro-body"><pre>CONTENT_MD</pre></td></tr></table><p><br /></p>""".stripMargin

  private val timeOut: FiniteDuration = 5.second

  private def getPageContentAsString(confluencePageId: Long): Future[String] = {
    val url = s"$confluenceRestUrl/content/$confluencePageId?expand=body.editor,version,space"
    wsClient.url(url).withAuth(user, password, WSAuthScheme.BASIC).withRequestTimeout(timeOut).get().map { response =>
      if (response.status == 200) {
        response.body
      } else {
        if (response.status == 504) {
          throw new Exception("Time out exception")
        } else {
          throw new Exception(s"Request to $url failed with code ${response.status}")
        }
      }
    }
  }

  def getConfluencePage(confluencePageId: Long): Future[Either[ConfluenceError, ConfluencePage]] = {
    getPageContentAsString(confluencePageId).map { response =>
      Right(parsePage(response))
    }.recoverWith {
      case NonFatal(e) => {
        Future.successful(Left(ConfluenceError(e.getMessage)))
      }
    }
  }

  def getConfluencePageVersion(confluencePageId: Long): Future[Either[ConfluenceError, Int]] = {
    getPageContentAsString(confluencePageId).map { response =>
      Right(parsePage(response).version.number)
    }.recoverWith {
      case NonFatal(e) => {
        Future.successful(Left(ConfluenceError(e.getMessage)))
      }
    }
  }

  def getChildrenConfluencePageTitle(parentConfluencePageId: Long): Future[Either[ConfluenceError, Seq[ConfluencePageTitle]]] = {
    val url = s"$confluenceRestUrl/content/$parentConfluencePageId/child?expand=page"
    wsClient.url(url).withAuth(user, password, WSAuthScheme.BASIC).withRequestTimeout(timeOut).get().map { response =>
      if (response.status == 200) {
        Right(Json.parse(response.body).as[ConfluencePageChildren].page.results)
      } else {
        Left(ConfluenceError(s"Unable to get children of the page ${parentConfluencePageId}: code ${response.status}: ${response.body}"))
      }
    }
  }

  def searchPagesByTitle(space: String, title: String): Future[Either[ConfluenceError, Seq[ConfluencePageTitle]]] = {
    val url = s"$confluenceRestUrl/content?spaceKey=${space}&title=${title}"
    wsClient.url(url).withAuth(user, password, WSAuthScheme.BASIC).withRequestTimeout(timeOut).get().map { response =>
      if (response.status == 200) {
        Right(Json.parse(response.body).as[ConfluenceSearch].results)
      } else {
        Left(ConfluenceError(s"Unable to search for a page by title : code ${response.status}: ${response.body}"))
      }
    }
  }

  def updateConfluencePageContentWithMarkdown(confluencePageId: Long, newTitle: String, newTitleContext: String, newMarkdown: ConfluenceMarkdownContent, status: String = "current"): Future[Either[ConfluenceError, ConfluencePage]] = {
    val newBody = pageWithMarkdownTemplate.replace("CONTENT_MD", newMarkdown.markdown)
    updateConfluencePageContent(confluencePageId, newTitle, newTitleContext, newBody, status)
  }

  def updateConfluencePageContent(confluencePageId: Long, newTitle: String, newTitleContext: String, newBody: String, status: String): Future[Either[ConfluenceError, ConfluencePage]] = {
    getConfluencePage(confluencePageId).map {
      case Right(originalPage) => {
        if (isUniqueTitle(newTitle, originalPage.title)) {
          updateConfluencePageWithNotExistingTitle(originalPage, originalPage.title, newBody, status)
        } else {
          searchPagesByTitle(originalPage.space.key, newTitle).map {
            case Left(error) => Future.successful(Left(error))
            case Right(results) => {
              if (results.isEmpty || results.nonEmpty && results.head.id.toLong == confluencePageId) {
                updateConfluencePageWithNotExistingTitle(originalPage, newTitle, newBody, status)
              } else {
                updateConfluencePageWithNotExistingTitle(originalPage, buildUniqueTitle(newTitle, newTitleContext), newBody, status)
              }
            }
          }
        }.flatten
      }
      case Left(e) => Future.successful(Left(e))
    }.flatten
  }

  private def updateConfluencePageWithNotExistingTitle(originalPage: ConfluencePage, newTitle: String, newBody: String, status: String): Future[Either[ConfluenceError, ConfluencePage]] = {
    val url = s"$confluenceRestUrl/content/${originalPage.id}?expand=body.editor,version,space"
    val newPageVersion = originalPage.version.copy(number = originalPage.version.number + 1)
    val newPage = ConfluencePage(id = originalPage.id, space = originalPage.space, title = newTitle, version = newPageVersion, body = ConfluencePageBody(editor = ConfluencePageBodyEditor(newBody)), status = status)
    val data = Json.stringify(Json.toJson(newPage))
    wsClient.url(url).withAuth(user, password, WSAuthScheme.BASIC)
      .withHttpHeaders(("Content-Type", "application/json"))
      .withRequestTimeout(timeOut)
      .put(data).map { response =>
      response.status match {
        case 200 => Right(parsePage(response.body))
        case _ => Left(ConfluenceError(response.body))
      }
    }
  }

  def createConfluencePage(space: String, parentConfluencePageId: Long, pageTitle: String, pageTitleContext: String, pageBody: String): Future[Either[ConfluenceError, ConfluencePage]] = {
    searchPagesByTitle(space, pageTitle).map {
      case Left(error) => Future.successful(Left(error))
      case Right(results) => {
        if (results.isEmpty) {
          createConfluencePageWithNotExistingTitle(space, parentConfluencePageId, pageTitle, pageBody)
        } else {
          createConfluencePageWithNotExistingTitle(space, parentConfluencePageId, buildUniqueTitle(pageTitle, pageTitleContext), pageBody)
        }
      }
    }.flatten
  }

  def createConfluencePageWithNotExistingTitle(space: String, parentConfluencePageId: Long, pageTitle: String, pageBody: String): Future[Either[ConfluenceError, ConfluencePage]] = {
    val bodyAsJson = ConfluencePageToCreate(space = ConfluenceSpace(space),
      title = pageTitle,
      version = ConfluencePageVersion(1),
      body = ConfluencePageBody(editor = ConfluencePageBodyEditor(pageBody)),
      ancestors = Seq(ConfluencePageId(parentConfluencePageId.toString)))
    val url = s"$confluenceRestUrl/content?expand=body.editor,version,space"
    wsClient.url(url).withAuth(user, password, WSAuthScheme.BASIC).withRequestTimeout(timeOut).post(Json.toJson(bodyAsJson)).map { response =>
      if (response.status == 200) {
        Right(parsePage(response.body))
      } else {
        Left(ConfluenceError(s"Request to $url failed with code ${response.status}"))
      }
    }
  }

  def deleteConfluencePage(confluencePageId: Long): Future[Either[ConfluenceError, Unit]] = {
    val url = s"$confluenceRestUrl/content/${confluencePageId}"
    wsClient.url(url).withAuth(user, password, WSAuthScheme.BASIC).withRequestTimeout(timeOut).delete().map { response =>
      if (response.status == 200) {
        Future.successful(Right(()))
      } else {
        if (response.status == 504) {
          Future.successful(Left(ConfluenceError("Time out exception")))
        } else {
          updateConfluencePageContent(confluencePageId, s"trashed ${System.currentTimeMillis()}", "", "", "trashed").map(_ => Right(()))
        }
      }
    }.flatten
  }

}

object ConfluenceClient {

  private val SEP = " ~ "

  def isUniqueTitle(originalTitle: String, actualTitle: String): Boolean = actualTitle.startsWith(s"${originalTitle}${SEP}")

  def buildUniqueTitle(newTitle: String, newTitleContext: String): String = s"${newTitle}${SEP}${newTitleContext}${SEP}${System.currentTimeMillis()}"

  def sameTitles(oneTitle: String, otherTitle: String): Boolean = oneTitle.equals(otherTitle) || oneTitle.startsWith(s"${otherTitle}${SEP}") || otherTitle.startsWith(s"${oneTitle}${SEP}")

  def parsePage(content: String): ConfluencePage = {
    Json.parse(content).as[ConfluencePage]
  }
}

