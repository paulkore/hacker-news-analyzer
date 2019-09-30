package analyzer

import org.scalatest.FunSpec

class HackerNewsAnalyzerSpec extends FunSpec {

  private val maxTopCommenters = 2

  private val singleStoryWithSingleComment = Seq(
    StoryWithComments(
      title = "story",
      comments = Seq(
        Comment(id = 1, username = "bozo")
      )
    )
  )

  private val singleStoryWithMultipleComments = Seq(
    StoryWithComments(
      title = "story",
      comments = Seq(
        Comment(id = 1, username = "hamster"),
        Comment(id = 2, username = "foobar"),
        Comment(id = 3, username = "bozo"),
        Comment(id = 4, username = "scooby"),
        Comment(id = 5, username = "foobar"),
        Comment(id = 6, username = "bozo"),
        Comment(id = 7, username = "bozo"),
      )
    )
  )

  private val multipleStoriesWithMultipleComments = Seq(
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


  describe("given empty input") {
    val result = HackerNewsAnalyzer.analyze(Seq(), maxTopCommenters)

    it("should produce zero stories with top commenters") {
      assert(result.storiesWithTopCommenters.isEmpty)
    }
    it("should produce empty total counts") {
      assert(result.commentCountByUsername.isEmpty)
    }
  }

  describe("given single story with single comment") {
    val result = HackerNewsAnalyzer.analyze(singleStoryWithSingleComment, maxTopCommenters)

    it ("should produce single story with single top commenter") {
      assertResult(result.storiesWithTopCommenters) {
        Seq(
          StoryWithTopCommenters("story", topCommenters = Seq(Commenter("bozo", 1)))
        )
      }
    }
    it ("should have a single commenter in the totals map") {
      assertResult(result.commentCountByUsername) {
        Map(
          "bozo" -> 1
        )
      }
    }
  }

  describe("given single story with multiple comments") {
    val result = HackerNewsAnalyzer.analyze(singleStoryWithMultipleComments, maxTopCommenters)

    it ("should produce single story with expected top commenters in descending order by comment count") {
      assertResult(result.storiesWithTopCommenters) {
        Seq(
          StoryWithTopCommenters("story", topCommenters = Seq(
            Commenter("bozo", 3),
            Commenter("foobar", 2),
          ))
        )
      }
    }

    it ("should have all commenters in the totals map, even if they are not top commenters") {
      assertResult(result.commentCountByUsername) {
        Map(
          "bozo" -> 3,
          "foobar" -> 2,
          "hamster" -> 1,
          "scooby" -> 1,
        )
      }
    }
  }

  describe("given multiple stories with multiple comments, some of which are by the same user") {
    val result = HackerNewsAnalyzer.analyze(multipleStoriesWithMultipleComments, maxTopCommenters)

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
