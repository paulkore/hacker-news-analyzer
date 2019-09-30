package analyzer

import scala.collection.immutable.ListMap
import scala.collection.mutable

object HackerNewsAnalyzer {

  def analyze(storiesWithComments: Seq[StoryWithComments]): AnalysisResult = {
    println("Analyzing data...")

    val commentsByUsername = mutable.Map[String, Int]()
    val storiesWithTopCommenters = mutable.ListBuffer[StoryWithTopCommenters]()

    storiesWithComments.foreach { story =>
      println(s"Analyzing story ${story.title} - ${story.comments.size} comments")

      val commentsByUsernameForStory = mutable.Map[String, Int]()
      story.comments.foreach { comment =>
        val username = comment.username
        incrementCommentCount(username, commentsByUsernameForStory)
        incrementCommentCount(username, commentsByUsername)
      }

      val commentersByCountDesc = ListMap(commentsByUsernameForStory.toSeq.sortWith(_._2 > _._2):_*)
      val topCommenters = commentersByCountDesc.take(10).map(c => Commenter(c._1, c._2)).toSeq

      storiesWithTopCommenters += StoryWithTopCommenters(
        title = story.title,
        topCommenters = topCommenters
      )
    }

    AnalysisResult(commentsByUsername.toMap, storiesWithTopCommenters)
  }

  def incrementCommentCount(username: String, commentsByUsername: mutable.Map[String, Int]) {
    commentsByUsername.get(username) match {
      case Some(count) => commentsByUsername.put(username, count+1)
      case None => commentsByUsername.put(username, 1)
    }
  }

}

case class StoryWithComments(
  title: String,
  comments: Seq[Comment],
)
object StoryWithComments {
  def apply(story: Story, comments: Seq[Comment]) = new StoryWithComments(
    title = story.title,
    comments = comments,
  )
}

case class AnalysisResult(
  commentCountByUsername: Map[String, Int],
  storiesWithTopCommenters: Seq[StoryWithTopCommenters],
)

case class StoryWithTopCommenters(
  title: String,
  topCommenters: Seq[Commenter],
)

case class Commenter(
  username: String,
  commentCountForStory: Int,
)

