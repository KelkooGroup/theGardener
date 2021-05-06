package services.clients

import play.api.Logging
import play.api.libs.json.Json
import play.api.libs.ws.{WSAuthScheme, WSClient}
import services.clients.ConfluenceClient.parsePage

import javax.inject._
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal


case class ConfluenceMarkdownContent(markdown: String)

case class ConfluenceError(error: String)

case class ConfluencePageBodyEditor(value: String, representation: String = "editor")

case class ConfluencePageBody(editor: ConfluencePageBodyEditor)

case class ConfluencePageVersion(number: Int)

case class ConfluencePage(id: String, title: String, version: ConfluencePageVersion, body: ConfluencePageBody, `type`: String = "page")

object ConfluencePage {
  implicit val formatConfluencePageBodyEditor = Json.format[ConfluencePageBodyEditor]
  implicit val formatConfluencePageBody = Json.format[ConfluencePageBody]
  implicit val formatConfluencePageVersion = Json.format[ConfluencePageVersion]
  implicit val formatConfluencePage = Json.format[ConfluencePage]
}


class ConfluenceClient @Inject()(wsClient: WSClient)(implicit ec: ExecutionContext) extends Logging {

  val user = "cop-doc-publisher"
  val password = "!Cop2021!"
  val confluenceUrl = "http://confluence.corp.kelkoo.net:8090/rest/api/content"
  val pageWithMarkdownTemplate =
    """<p class="auto-cursor-target"><br /></p><table class="wysiwyg-macro" data-macro-name="markdown" data-macro-id="27a98bc4-0ea3-4461-b991-89a1fd294410" data-macro-parameters="allowHtml=true|atlassian-macro-output-type=INLINE|id=THE_GARDENER_MD" data-macro-schema-version="1" style="background-image: url(http://confluence.corp.kelkoo.net:8090/plugins/servlet/confluence/placeholder/macro-heading?definition=e21hcmtkb3duOmFsbG93SHRtbD10cnVlfGlkPVRIRV9HQVJERU5FUl9NRHxhdGxhc3NpYW4tbWFjcm8tb3V0cHV0LXR5cGU9SU5MSU5FfQ&amp;locale=en_GB&amp;version=2); background-repeat: no-repeat;" data-macro-body-type="PLAIN_TEXT"><tr><td class="wysiwyg-macro-body"><pre>CONTENT_MD</pre></td></tr></table><p><br /></p>""".stripMargin

  def getPageContentAsString(pageId: Int): Future[String] = {
    val url = s"$confluenceUrl/$pageId?expand=body.editor,version"
    wsClient.url(url).withAuth(user, password, WSAuthScheme.BASIC).withRequestTimeout(3.second).get().map { response =>
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

  def getPageContent(pageId: Int): Future[Either[ConfluenceError, ConfluencePage]] = {
    getPageContentAsString(pageId).map { response =>
      Right(parsePage(response))
    }.recoverWith {
      case NonFatal(e) => {
        Future.successful(Left(ConfluenceError(e.getMessage)))
      }
    }
  }

  def updatePageContent(pageId: Int, newTitle: String, newMarkdown: ConfluenceMarkdownContent): Future[Either[ConfluenceError, Unit]] = {
    val newBody = pageWithMarkdownTemplate.replace("CONTENT_MD",newMarkdown.markdown)
    updatePageContent(pageId, newTitle, newBody)
  }

  def updatePageContent(pageId: Int, newTitle: String, newBody: String): Future[Either[ConfluenceError, Unit]] = {
    getPageContent(pageId).map {
      _ match {
        case Right(originalPage) => updatePageContent(originalPage, newTitle, newBody)
        case Left(e) => Future.successful(Left(e))
      }
    }.flatten
  }

  def updatePageContent(originalPage: ConfluencePage, newTitle: String, newBody: String): Future[Either[ConfluenceError, Unit]] = {
    val url = s"$confluenceUrl/${originalPage.id}"
    val newPageVersion = originalPage.version.copy(number = originalPage.version.number + 1)
    val newPage = ConfluencePage(id = originalPage.id, title = newTitle, version = newPageVersion, body = ConfluencePageBody(editor = ConfluencePageBodyEditor(newBody)))
    val data = Json.stringify(Json.toJson(newPage))

    System.out.println(data)
    wsClient.url(url).withAuth(user, password, WSAuthScheme.BASIC)
      .withHttpHeaders(("Content-Type", "application/json"))
      .withRequestTimeout(3.second)
      .put(data).map { response =>
      response.status match {
        case 200 => Right(())
        case _ => Left(ConfluenceError(response.body))
      }
    }
  }


}


object ConfluenceClient {
  def parsePage(content: String): ConfluencePage = {
    Json.parse(content).as[ConfluencePage]
  }
}

