package analyzer

/**
  * This component adds a layer on top of the Hacker Hews API,
  * that takes care of model abstraction and parellel processing.
  */
trait HackerNewsService {
  def getTopStoryIds(limit: Int): Seq[Long]
  def getStories(storyIds: Seq[Long]): Seq[Story]
  def getComments(commentIds: Seq[Long]): Seq[Comment]
  def getStory(storyId: Long): Option[Story]
  def getComment(commentId: Long): Option[Comment]
}

object HackerNewsService {

  private val api = HackerNewsAPI()

  /**
    * Concurrency factor chosen based on basic benchmarking
    */
  private val ConcurrencyFactor = 12

  def apply(): HackerNewsService = new HackerNewsService {

    override def getTopStoryIds(limit: Int): Seq[Long] =
      api.getTopStoryIds.take(limit)

    override def getStories(storyIds: Seq[Long]): Seq[Story] =
      loadInParallel[Story](storyIds) { getStory }

    override def getComments(commentIds: Seq[Long]): Seq[Comment] =
      loadInParallel[Comment](commentIds) { getComment }

    override def getStory(storyId: Long): Option[Story] = {
      api.getItem(storyId) match {
        case Some(item) => Some(Story(item))
        case None => None
      }
    }

    override def getComment(commentId: Long): Option[Comment] = {
      api.getItem(commentId) match {
        case Some(item) => Some(Comment(item))
        case None => None
      }
    }

    private def loadInParallel[T](ids: Seq[Long])(loadForId: Long => Option[T]): Seq[T] = {
      ids.grouped(ConcurrencyFactor).flatMap { ids =>
        ids.par.flatMap {
          id => loadForId(id)
        }.toIndexedSeq
      }.toSeq
    }
  }

}

case class Story(
  id: Long,
  title: String,
  commentIds: Seq[Long],
)
object Story {
  def apply(item: Item): Story = {
    require(item.`type` == "story")
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
  def apply(item: Item): Comment = {
    require(item.`type` == "comment")
    new Comment(
      id = item.id,
      username = item.by.getOrElse("N/A"),
    )
  }
}
