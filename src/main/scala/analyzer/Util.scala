package analyzer

object Util {

  def time[R](label: String)(block: => R): R = {
    val t0 = System.currentTimeMillis()
    val result = block
    val t1 = System.currentTimeMillis()
    println(s"$label - elapsed time: " + (t1 - t0) + "ms")
    result
  }

}
