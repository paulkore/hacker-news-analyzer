package analyzer

object Main extends App {

  private val hackerNews = HackerNewsService()

  analyze()

  private def analyze(): Unit = {

    val storiesWithComments = Util.time("Load datafrom Hacker News API") { loadStoriesWithComments() }

    // TODO: this step seems to be slower than it should be, look into why
    println("Analyzing data...")
    val analysisResult = HackerNewsAnalyzer.analyze(storiesWithComments)

    Output.outputResult(analysisResult)
  }

  private def loadStoriesWithComments(): Seq[StoryWithComments] = {
    println("Loading top story IDs...")
    val topStoryIds = hackerNews.getTopStoryIds(limit = 30)

    println("Loading stories...")
    val stories = hackerNews.getStories(topStoryIds)

    println("Loading comments for all stories...")
    stories.map { story =>
      val comments = hackerNews.getComments(story.commentIds)
      StoryWithComments(story, comments)
    }
  }

}


