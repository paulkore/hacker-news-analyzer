package analyzer

import org.scalatest.FunSpec

class HackerNewsAnalyzerSpec extends FunSpec {

  private val maxTopCommenters = 2

  private val data = Seq(
    StoryWithComments(
      title = "story1",
      comments = Seq(
        Comment(id = 1, username = "hamster"),
        Comment(id = 2, username = "foobar"),
        Comment(id = 3, username = "bozo"),
        Comment(id = 4, username = "scooby"),
        Comment(id = 5, username = "foobar"),
        Comment(id = 6, username = "bozo"),
        Comment(id = 7, username = "bozo"),
      )
    ),
    StoryWithComments(
      title = "story2",
      comments = Seq(
        Comment(id = 2, username = "flip"),
        Comment(id = 3, username = "bozo"),
        Comment(id = 4, username = "barney"),
        Comment(id = 5, username = "flip"),
        Comment(id = 6, username = "bozo"),
        Comment(id = 7, username = "bozo"),
      )
    ),
  )

  describe("given multiple stories with multiple comments, some of which are by the same user") {
    val result = HackerNewsAnalyzer.analyze(data, maxTopCommenters)

    it("should produce multiple stories with expected top commenters in descending order by comment count") {
      assertResult(result.storiesWithTopCommenters) {
        Seq(
          StoryWithTopCommenters("story1", topCommenters = Seq(
            Commenter("bozo", 3),
            Commenter("foobar", 2),
          )),
          StoryWithTopCommenters("story2", topCommenters = Seq(
            Commenter("bozo", 3),
            Commenter("flip", 2),
          )),
        )
      }
    }

    it ("should have all commenters in the totals map, even if they are not top commenters, and totals should span over all stories") {
      assertResult(result.commentCountByUsername) {
        Map(
          "bozo" -> 6,
          "foobar" -> 2,
          "flip" -> 2,
          "hamster" -> 1,
          "scooby" -> 1,
          "barney" -> 1,
        )
      }
    }

  }

}
