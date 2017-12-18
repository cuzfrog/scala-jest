package sjest.impl

/** Stands for single test suite result, used to communicate between master and slave runner */
private case class TestCaseResult(failed: Int, passed: Int, isSuccessfulSuite: Boolean = true) {
  def total: Int = failed + passed
  def toJson: String = s"{failed:$failed,passed:$passed,isSuccessfulSuite:$isSuccessfulSuite}"
}

private object TestCaseResult {
  private final val Extractor = """{failed:(\d+),passed:(\d+),isSuccessfulSuite:(true|false)}""".r
  def fromJson(in: String): TestCaseResult = in.replaceAll("""[\s\n\r]""", "") match {
    case Extractor(failedStr, passedStr, isSuccessfulSuiteStr) =>
      TestCaseResult(failedStr.toInt, passedStr.toInt, isSuccessfulSuiteStr.toBoolean)
    case bad => throw new IllegalArgumentException(s"Cannot parse json:$bad")
  }
}