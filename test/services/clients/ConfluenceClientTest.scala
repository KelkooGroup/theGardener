package services.clients

import akka.actor.ActorSystem
import org.scalatestplus.play.PlaySpec

import scala.io.Source
import play.api.{Configuration, Environment}
import play.api.test.WsTestClient

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class ConfluenceClientTest extends PlaySpec {

  implicit val actorSystem: ActorSystem = ActorSystem("ConfluenceClientTest")

  val testPageId = 109379645L

  "ConfluenceClientTest" should {

    "parse page content" in {
      val content =ConfluenceClientTest.pageWithMarkdown
      assert(ConfluenceClient.parsePage(content).title.equals("TEST REST API WITH MD"))
    }

    "get page - Integration test" ignore  {
      val testPageId = 109379647L
      WsTestClient.withClient { client =>
        val configuration = Configuration.load(Environment.simple())
        val confluenceClient = new ConfluenceClient(configuration, client)

        val page = Await.result(confluenceClient.getConfluencePage(testPageId), 10.seconds)

        System.out.print(page)
        page.isRight mustEqual (true)
        page.map(page => page.body.editor.value mustEqual ("<p>TEST TEST TEST</p>"))
      }
    }

    "get page version - Integration test" ignore {
      val testPageId = 109379649L
      WsTestClient.withClient { client =>
        val configuration = Configuration.load(Environment.simple())
        val confluenceClient = new ConfluenceClient(configuration, client)

        val version = Await.result(confluenceClient.getConfluencePageVersion(testPageId), 10.seconds)

        System.out.print(version)
        version.isRight mustEqual (true)
        version.map(version => version mustEqual (2))
      }
    }

    "update page content - Integration test" ignore {
      val testPageId = 109379652L
      WsTestClient.withClient { client =>
        val configuration = Configuration.load(Environment.simple())
        val confluenceClient = new ConfluenceClient(configuration, client)

        val status = Await.result(confluenceClient.updateConfluencePageContentWithMarkdown(testPageId, "update page content", "",ConfluenceMarkdownContent(ConfluenceClientTest.markDownFromFile)), 10.seconds)

        System.out.print(status)
        status.isRight mustEqual (true)
      }
    }

    "get children - Integration test" ignore {
      WsTestClient.withClient { client =>
        val configuration = Configuration.load(Environment.simple())
        val confluenceClient = new ConfluenceClient(configuration, client)

        val children = Await.result(confluenceClient.getChildrenConfluencePageTitle(testPageId), 10.seconds)
        System.out.print(children)
        children.isRight mustEqual (true)
        children.map(titles => titles.map(_.title).contains("get page") mustEqual (true))
      }
    }

    "exists create delete page content - Integration test" ignore {
      val space = "EN"
      val testPageId = 109379657L
      val pageTitle = "temp page"
      val pageBody = "temp body"
      WsTestClient.withClient { client =>
        val configuration = Configuration.load(Environment.simple())
        val confluenceClient = new ConfluenceClient(configuration, client)

        val children = Await.result(confluenceClient.getChildrenConfluencePageTitle(testPageId), 10.seconds)
        System.out.print(children)
        children.isRight mustEqual (true)
        children.map { titles =>
          titles.map(_.title).contains(pageTitle) mustEqual (false)

          val status = Await.result(confluenceClient.createConfluencePage(space, testPageId, pageTitle, "", pageBody), 10.seconds)
          System.out.print(status)
          status.isRight mustEqual (true)


          val children = Await.result(confluenceClient.getChildrenConfluencePageTitle(testPageId), 10.seconds)
          System.out.print(children)
          children.isRight mustEqual (true)
          children.map { titles =>
            titles.map(_.title).contains(pageTitle) mustEqual (true)
            val tempPageId = titles.find(_.title == pageTitle).get.id.toLong

            val status = Await.result(confluenceClient.deleteConfluencePage(tempPageId), 10.seconds)
            System.out.print(status)
            status.isRight mustEqual (true)
          }
        }
      }
    }
  }

}


object ConfluenceClientTest {

  val pageWithMarkdown = """{"id":"130913238","type":"page","status":"current","title":"TEST REST API WITH MD","space":{"key":"EN"},"version":{"by":{"type":"known","username":"cop-doc-publisher","userKey":"8acce1bf783462a401783bc8d9cc0001","profilePicture":{"path":"/images/icons/profilepics/default.svg","width":48,"height":48,"isDefault":true},"displayName":"Cop Doc Publisher","_links":{"self":"http://confluence.corp.kelkoo.net:8090/rest/api/user?key=8acce1bf783462a401783bc8d9cc0001"},"_expandable":{"status":""}},"when":"2021-05-05T07:53:12.183Z","message":"","number":9,"minorEdit":false,"hidden":false,"_links":{"self":"http://confluence.corp.kelkoo.net:8090/rest/experimental/content/130913238/version/9"},"_expandable":{"content":"/rest/api/content/130913238"}},"body":{"editor":{"value":"<p class=\"auto-cursor-target\"><br /></p><table class=\"wysiwyg-macro\" data-macro-name=\"markdown\" data-macro-id=\"27a98bc4-0ea3-4461-b991-89a1fd294410\" data-macro-parameters=\"allowHtml=true|atlassian-macro-output-type=INLINE|id=THE_GARDENER_MD\" data-macro-schema-version=\"1\" style=\"background-image: url(http://confluence.corp.kelkoo.net:8090/plugins/servlet/confluence/placeholder/macro-heading?definition=e21hcmtkb3duOmFsbG93SHRtbD10cnVlfGlkPVRIRV9HQVJERU5FUl9NRHxhdGxhc3NpYW4tbWFjcm8tb3V0cHV0LXR5cGU9SU5MSU5FfQ&amp;locale=en_GB&amp;version=2); background-repeat: no-repeat;\" data-macro-body-type=\"PLAIN_TEXT\"><tr><td class=\"wysiwyg-macro-body\"><pre>Audience: DEV EDITas Documentation writer\n\n### Documentation source 2222 \n \nThere are several sources: \n\n| **Item**          |     **Source**     |\n| :------------ | :-------------- |\n| Pages under Publisher tab     | http://gitlab.corp.kelkoo.net/syndication/thegardener-publisher-data/tree/master/documentation         |\n| Projects     | Publisher projects own documentation       |\n| Architecture     | http://gitlab.corp.kelkoo.net/syndication/thegardener-publisher-data/tree/master/documentation/Architecture         |\n| Documentation Trust Agreement     | http://gitlab.corp.kelkoo.net/syndication/thegardener-publisher-data/tree/master/documentation/DTA         |\n| Guides     | http://gitlab.corp.kelkoo.net/syndication/thegardener-publisher-data/tree/master/documentation/Guides         |\n \nEach item is considered as a theGardener project. The documentation writer need to follow theGardener documentation format. \n\nThe global projects (Publisher tab, Architecture, Documentation Trust Agreement, Guides) are already setup to display the documentation on the engineering instance under [Publisher documentation start point](http://thegardener.corp.kelkoo.net/app/documentation/navigate/_publisher;path=publisherSystems%3Emaster%3E_).\n\nAll new projects under the section \"Projects\" need to be registered properly under theGardener engineering instance. \n\n**The source of the documentation is in the same git repository as the code source.** \nDo not create a specific branch for the documentation unless you are doing only a documentation task, in this case use the name ```documentation``` so that it can appear on the UI.\nThe recommendation is to write and review code and documentation at the same time on the same branch.      \n\n### Register a new project \n\n#### Which project can we added in the Publisher documentation ?\n\n   - The project need to be a publisher project or a common project used by the publisher team. In theGardener, we can put project at several places in the hierarchy, so we can put common project in the publisher hierarchy \n      - Decide with the team if it make sense to add it or not and where.\n   - The project need to match the [Documentation Trust Agreement](thegardener://navigate/_publisher/publisherGuides/master/_DTA/Dta) at project level.\n      - You can clone [the template](http://gitlab.corp.kelkoo.net/syndication/thegardener-publisher-data/tree/master/documentation/DTA/Template) as a starting point.    \n        \n#### Where in the publisher hierarchy ? \n\n| **Id**          |     **Hierarchy**     |\n| :------------ | :-------------- |\n| .01. |  Publisher     |  \n| .01.01. |  Publisher / Projects     |          \n| .01.01.01. | Publisher / Projects / Services (internal and external)    |        \n| .01.01.02. | Publisher / Projects / Extranet     |        \n| .01.01.03. | Publisher /  Projects / Management     |        \n| .01.01.04. | Publisher /  Projects / Libraries     |\n| .01.01.05. | Publisher /  Projects / Tools     |\n| .01.01.06. | Publisher /  Projects / Service (as developers.kkg.com)     |\n     \nDecide with the team where to put it or if a new organisation of the publisher hierarchy is needed.\n\n\n### Register a new project in theGardener\n\nOn [theGardener Configure guide](https://thegardener.kelkoogroup.com/app/documentation/navigate/_doc;path=theGardener%3Emaster%3E_guides_/configure), apply the sections:\n   - Projects\n   - Link between projects and hierarchy\n   - Hooks on the git servers\n\nOn the Engineering instance:\n   - [Register project](http://thegardener.corp.kelkoo.net/api/docs/#/ProjectController/registerProject)\n   - [Link project to hierarchy](http://thegardener.corp.kelkoo.net/api/docs/#/ProjectController/linkProjectToHierarchy)\n\nWith the following settings:\n\n#### Project\n\nField | Value recommended in the publisher context \n------------ | ------------- \nid | Example \"leadService\" \nname | Example \"Lead Service\" \nrepositoryUrl | Example  \"http://gitlab.corp.kelkoo.net/syndication/kls.git\" \nstableBranch | \"qa\" \ndisplayedBranches | \"qa\" OR \"master\" OR \"documentation\" OR \"feature.*\" OR \"bugfix.*\" to display all branches in the documentation. This allow us to visualize the output of the documentation before the merge.  \nfeaturesRootPath | \"test/features\", usually the BDD scenario are there. \ndocumentationRootPath | \"documentation\", let's always use this directory for the source of the documentation. \n\nExample with lead service\n```json\n{\n  \"id\": \"leadService\",\n  \"name\": \"Lead service\",\n  \"repositoryUrl\": \"http://gitlab.corp.kelkoo.net/syndication/kls.git\",\n  \"sourceUrlTemplate\": \"http://gitlab.corp.kelkoo.net/syndication/kls/-/blob/${branch}/${path}\",\n  \"stableBranch\": \"qa\",\n  \"displayedBranches\": \"qa|master|documentation|feature.*|bugfix.*\",\n  \"featuresRootPath\": \"test/features\",\n  \"documentationRootPath\": \"documentation\"\n}\n```\n   \n#### Link between projects and hierarchy\n\nHierarchy id: use one of  `.01.01.xx.`\n  \n#### Hooks on the git servers  \n  \nAdd a webhook between GitLab and theGardener to be able to have the direct feedback of a commit on the UI.\n\n![webhook](http://thegardener.corp.kelkoo.net/api/assets?path=publisherGuides%3Emaster%3E/DocumentationWriter/../assets/images/gitlab_WebHook.png)\n\nFor instance, with leadService:  \n- On http://gitlab.corp.kelkoo.net/syndication/kls/-/settings/integrations add webhook on \"Push Events\"\n- To http://thegardener.corp.kelkoo.net/api/projects/leadService/synchronize  \n  \n### theGardener admin guide\n\nFollow [theGardener admin guide](https://thegardener.kelkoogroup.com/app/documentation/navigate/_doc/theGardener/master/_Admin) for more details.</pre></td></tr></table><p><br /></p><p><br /></p><hr /><p class=\"auto-cursor-target\">Sourced from <a href=\"http://thegardener.corp.kelkoo.net/app/documentation/navigate/_publisher/publisherGuides/_/_DocumentationWriter/ConfigureDocumentationGuide\">http://thegardener.corp.kelkoo.net</a></p>","representation":"storage","_expandable":{"webresource":"","content":"/rest/api/content/130913238"}},"_expandable":{"view":"","export_view":"","styled_view":"","storage":"","anonymous_export_view":""}},"extensions":{"position":"none"},"_links":{"webui":"/display/~reinharg/TEST+REST+API+WITH+MD","edit":"/pages/resumedraft.action?draftId=130913238&draftShareId=65026ce2-bbfa-4eef-8263-c6cde15afd97","tinyui":"/x/1pPNBw","collection":"/rest/api/content","base":"http://confluence.corp.kelkoo.net:8090","context":"","self":"http://confluence.corp.kelkoo.net:8090/rest/api/content/130913238"},"_expandable":{"container":"/rest/api/space/~reinharg","metadata":"","operations":"","children":"/rest/api/content/130913238/child","restrictions":"/rest/api/content/130913238/restriction/byOperation","history":"/rest/api/content/130913238/history","ancestors":"","descendants":"/rest/api/content/130913238/descendant","space":"/rest/api/space/~reinharg"}}"""

  val markdownContent =
    """### Register a new project
      |
      |#### Which project can we added in the Publisher documentation ?
      |
      |   - The project need to be a publisher project or a common project used by the publisher team. In theGardener, we can put project at several places in the hierarchy, so we can put common project in the publisher hierarchy
      |      - Decide with the team if it make sense to add it or not and where.
      |""".stripMargin

  def markDownFromFile: String = Source.fromFile("documentation/Meta.md").mkString

}