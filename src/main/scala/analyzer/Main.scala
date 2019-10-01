package analyzer

object Main extends App {

  Util.time("Entire program") {

    println("Loading data...")
    val storiesWithComments = Util.time("Load stories with comments") {
      HackerNewsService.getTopStoriesWithComments(limit = 30)
    }

    println()
    println("Analyzing data...")
    val analysisResult = HackerNewsAnalyzer.analyze(storiesWithComments, topCommentersCount = 10)
    Output.outputResult(analysisResult)

    println()
  }

}


