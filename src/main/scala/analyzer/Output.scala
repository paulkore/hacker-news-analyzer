package analyzer

object Output {

  def outputResult(analysisResult: AnalysisResult): Unit = {

    Line()
      .add("Story")
      .add("1st Top Commenter")
      .add("2nd Top Commenter")
      .add("3rd Top Commenter")
      .add("4th Top Commenter")
      .add("5th Top Commenter")
      .add("6th Top Commenter")
      .add("7th Top Commenter")
      .add("8th Top Commenter")
      .add("9th Top Commenter")
      .add("10th Top Commenter")
      .print()

    Line()
      .add("")
      .add("")
      .add("")
      .add("")
      .add("")
      .add("")
      .add("")
      .add("")
      .add("")
      .add("")
      .add("")
      .print()

    analysisResult.storiesWithTopCommenters.foreach { story =>

      val line = Line().add(story.title)

      story.topCommenters.foreach { commenter =>
        val username = commenter.username
        val storyComments = commenter.commentCountForStory
        val totalComments = analysisResult.commentCountByUsername.get(username).getOrElse()
        line.add(s"$username ($storyComments for story - $totalComments total)")
      }

      val emptyColumns = 10 - story.topCommenters.size
      for (_ <- 1 to emptyColumns) {
        line.add("")
      }
      line.print()
    }
  }

}

private class Line {

  private val firstColumnSize = 90
  private val defaultColumnSize = 45

  private var text: String = "|"
  private var first = true

  def add(cellText: String): Line = {
    val columnSize = if (first) firstColumnSize else defaultColumnSize
    text += " " + cellText.padTo(columnSize, ' ') + " |"
    first = false
    this
  }

  def print(): Unit = {
    println(text)
  }

}
private object Line {
  def apply() = new Line()
}
