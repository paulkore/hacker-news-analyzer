package analyzer

object Main extends App {

  private val hackerNews = HackerNewsService()

  Util.time("\n\nTotal run time") { analyze() }

  private def analyze(): Unit = {

    val storiesWithComments = loadStoriesWithComments()

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


