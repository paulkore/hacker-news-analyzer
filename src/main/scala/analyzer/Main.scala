package analyzer

object Main extends App {

  private[this] val hackerNews = HackerNewsService()

  analyze()

  private def analyze(): Unit = {

    // TODO: review concurrency aspect if there's time

    val data = Util.time("Load data from Hacker News API") { loadData() }

    // TODO: aggregate and output results
  }

  private def loadData(): Seq[StoryWithComments] = {
    // TODO: increase to 30 stories

    println("Loading top story IDs...")
    val topStoryIds = hackerNews.getTopStoryIds(limit = 5)

    println("Loading stories...")
    val stories = hackerNews.getStories(topStoryIds)

    println("Loading comments for all stories...")
    stories.map { story =>
      val comments = hackerNews.getComments(story.commentIds)
      StoryWithComments(story, comments)
    }
  }
}

case class StoryWithComments(
  id: Long,
  title: String,
  comments: Seq[Comment]
)
object StoryWithComments {
  def apply(story: Story, comments: Seq[Comment]) = new StoryWithComments(
    id = story.id,
    title = story.title,
    comments = comments,
  )
}
