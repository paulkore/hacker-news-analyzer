package analyzer

import java.net.URL
import skinny.http._
import skinny.json4s.JSONStringOps._
import scala.util._

/**
  * This is a direct view on the Hacker News API.
  * Each method in this component corresponds to one operation in the API.
  */
trait HackerNewsAPI {
  def getTopStoryIds: Seq[Long]
  def getItem(itemId: Long): Option[Item]
}
object HackerNewsAPI {

  def apply(): HackerNewsAPI = new HackerNewsAPI {

    override def getTopStoryIds: Seq[Long] = {
      val response = httpGet("/topstories.json")
      response.status match {
        case 200 => parseResponseBody[Seq[Long]](response)
        case _ => throw UnhandledHttpStatusException(response)
      }
    }

    override def getItem(id: Long): Option[Item] = {
      val response = httpGet(s"/item/$id.json")
      response.status match {
        case 200 => Some(parseResponseBody[Item](response))
        case 404 => None
        case _ => throw UnhandledHttpStatusException(response)
      }
    }

    private[this] def httpGet(contextUrl: String): Response = {
      val baseUrl = "https://hacker-news.firebaseio.com/v0"
      val url = new URL(baseUrl + contextUrl)
      HTTP.get(new Request(url.toString))
    }

    private[this] def parseResponseBody[T](response: Response)(implicit m: Manifest[T]): T = {
      fromJSONString[T](response.textBody) match {
        case Success(body) => body
        case Failure(error) => {
          throw new RuntimeException(s"Failed to parse response: ${response.textBody}", error)
        }
      }
    }
  }
}

case class UnhandledHttpStatusException(response: Response)
  extends RuntimeException(s"Received HTTP status ${response.status}, response body: ${response.textBody}")

case class Item(
  id: Long,
  `type`: String,
  title: Option[String],
  by: Option[String],
  kids: Seq[Long],
)
