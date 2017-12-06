package sjest.impl

/** Stands for single test suite result, used to communicate between master and slave runner */
private case class TestCaseResult(failed: Int, passed: Int) {
  def total: Int = failed + passed
  def toJson: String = s"{'failed':$failed,'passed':$passed}"
}

private object TestCaseResult {
  private final val Extractor = """{'failed':(\d+),'passed':(\d+)}""".r
  def fromJson(in: String): TestCaseResult = in.replaceAll("""[\s\n\r]""", "") match {
    case Extractor(failedStr, passedStr) => TestCaseResult(failedStr.toInt, passedStr.toInt)
    case bad => throw new IllegalArgumentException(s"Cannot parse json:$bad")
  }
}