package sjest.impl

import com.sun.istack.internal.Nullable
import sjest.support.Stateless

private case class TestCaseResult(failed: Int, passed: Int) {
  def total: Int = failed + passed
}

@Stateless
private sealed trait JestOutputParser {
  /**
   * Extract test statistics info and update the TestStatistics
   */
  @throws[IllegalArgumentException]
  def extractStatistics(jestOutput: String): TestCaseResult
}

private final class JestOutputParserImpl extends JestOutputParser {

  import JestOutputParserImpl._

  override def extractStatistics(jestOutput: String): TestCaseResult = {
    val lines = extractLines(jestOutput)
    extractTestResult(lines)
  }
}

private object JestOutputParserImpl {
  private final val ansiRegex = fansi.Str.ansiRegex.pattern()
  private final val ResultExtractor = """Tests:( \d+ failed,)?( \d+ passed,)? \d+ total""".r

  private def extractLines(jestOutput: String): Seq[String] = {
    val plainOutput = jestOutput.replaceAll(ansiRegex, "")
    val qualifiedLines = plainOutput.split(raw"""$NEWLINE""").toSeq
    val lines = qualifiedLines.collect {
      case line if line.startsWith("Tests:") => line.replaceAll("""\s\s+""", " ")
    }
    require(lines.lengthCompare(0) > 0,
      s"Cannot extract test result from jest output:$NEWLINE$jestOutput")
    lines
  }

  //contract: lines.size>0
  private def extractTestResult(lines: Seq[String]): TestCaseResult = {
    val resultOpt = lines.collectFirst {
      case ResultExtractor(failedStr, passedStr) =>
        val failed = extractNum(failedStr)
        val passed = extractNum(passedStr)
        TestCaseResult(failed, passed)
    }
    resultOpt.getOrElse(throw new IllegalArgumentException(
      ("Cannot parse test result from jest output:" +: lines).mkString(NEWLINE)))
  }

  private final val NumberExtractor = """(\d+) [a-zA-Z]+,""".r
  private def extractNum(@Nullable expr: String): Int = {
    if (expr == null) 0
    else expr.trim match {
      case NumberExtractor(numStr) => numStr.toInt
      case bad => throw new IllegalArgumentException(s"Cannot extract num from '$bad'")
    }
  }
}