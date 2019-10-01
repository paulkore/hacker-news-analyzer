package analyzer

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.blocking
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * This component adds a layer on top of the Hacker Hews API,
  * that takes care of model abstraction and parellel processing.
  */
object HackerNewsService {

  def getTopStoriesWithComments(limit: Int): Seq[StoryWithComments] = {
    HackerNewsAPI.getTopStoryIds
      .take(limit)
      .map(getStory)
      .map { _.map {
        case Some(story) => {
          val comments = story.commentIds
            .map(getComment)
            .flatMap(Await.result(_, 5.seconds))

          Some(StoryWithComments(story.title, comments))
        }
        case None => None
      }}
      .flatMap { Await.result(_, 30.seconds) }
  }

  private def getStory(storyId: Long): Future[Option[Story]] = {
    Future {
      blocking {
        HackerNewsAPI.getItem(storyId)
      } match {
        case Some(item) => {
          // TODO: investigate - not all items are stories
          if (Story.isStory(item)) Some(Story(item)) else None
        }
        case None => None
      }
    }
  }

  private def getComment(commentId: Long): Future[Option[Comment]] = {
    Future {
      blocking {
        HackerNewsAPI.getItem(commentId)
      } match {
        case Some(item) => {
          // TODO: investigate - not all items are comments
          if (Comment.isComment(item)) Some(Comment(item)) else None
        }
        case None => None
      }
    }
  }

}

case class Story(
  id: Long,
  title: String,
  commentIds: Seq[Long],
)
object Story {

  def isStory(item: Item): Boolean =
    item.`type` == "story" &&
    // TODO: investigate - sometimes stories have no title (weird)
    item.title.isDefined

  def apply(item: Item): Story = {
    require(isStory(item))
    new Story(
      id = item.id,
      title = item.title.getOrElse("N/A"),
      commentIds = item.kids
    )
  }
}

case class Comment(
  id: Long,
  username: String
)
object Comment {

  def isComment(item: Item): Boolean =
    item.`type` == "comment" &&
    // TODO: investigate - sometimes comments don't have a username (weird)
    item.by.isDefined

  def apply(item: Item): Comment = {
    try {
      require(isComment(item))
      new Comment(
        id = item.id,

        username = item.by.getOrElse("N/A"),
      )
    } catch {
      case ex: Exception => {
        throw ex
      }
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
