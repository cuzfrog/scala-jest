package sjest.impl

import sjest.support.SideEffect

private sealed trait JestOutputParser {
  /**
   * Extract test statistics info and update the TestStatistics
   */
  @SideEffect
  def extractStatistics(jestOutput: String,
                        testStatistics: TestStatistics): Unit
}

private final class JestOutputParserImpl extends JestOutputParser {
  override def extractStatistics(jestOutput: String,
                                 testStatistics: TestStatistics): Unit = {
    //todo: impl
  }
}