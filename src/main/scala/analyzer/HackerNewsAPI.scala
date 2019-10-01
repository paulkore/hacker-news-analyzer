package analyzer

import java.net.URL
import skinny.http._
import skinny.json4s.JSONStringOps._
import scala.util._

/**
  * This is a direct view on the Hacker News API.
  * Each method in this component corresponds to one operation in the API.
  */
object HackerNewsAPI {

  def getTopStoryIds: Seq[Long] = {
    val response = httpGet("/topstories.json")
    response.status match {
      case 200 => parseResponseBody[Seq[Long]](response) match {
        case Some(ids) => ids
        case None => Nil
      }
      case _ => throw UnhandledHttpStatusException(response)
    }
  }

  def getItem(id: Long): Option[Item] = {
    val response = httpGet(s"/item/$id.json")
    response.status match {
      case 200 => parseResponseBody[Item](response)
      case 404 => None
      case _ => throw UnhandledHttpStatusException(response)
    }
  }

  private[this] def httpGet(contextUrl: String): Response = {
    val baseUrl = "https://hacker-news.firebaseio.com/v0"
    val url = new URL(baseUrl + contextUrl)
    HTTP.get(new Request(url.toString))
  }

  private[this] def parseResponseBody[T](response: Response)(implicit m: Manifest[T]): Option[T] = {
    if (response.textBody.isEmpty) {
      // TODO: investigate - sometimes there's an empty response body
      None
    } else {
      fromJSONString[T](response.textBody) match {
        case Success(obj) => {
          // TODO: investigate - sometimes the parsed object is NULL
          Option(obj)
        }
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
