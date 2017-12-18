package sjest.impl

import sbt.testing.Status
import sjest.support.{Service, Stateful}

import scala.collection.mutable.ArrayBuffer

@Service
@Stateful
private sealed trait TestStatistics {
  @throws[IllegalArgumentException]
  def incrementPassedTest(n: Int = 1): Unit
  @throws[IllegalArgumentException]
  def incrementFailedTest(n: Int = 1): Unit
  def nextTestSuite(status: Status): Unit

  def totalTestCnt: Int
  def passedTestCnt: Int
  def failedTestCnt: Int

  def totalSuiteCnt: Int
  def passedSuiteCnt: Int
  def failedSuiteCnt: Int

  def isAllPassed: Boolean = totalSuiteCnt == passedSuiteCnt

  def testsReport(ansi: Boolean = true): String
  def suitesReport(ansi: Boolean = true): String
  def report(ansi: Boolean = true): String = suitesReport(ansi) + NEWLINE + testsReport(ansi)

  def getSuites: Seq[TestCaseResult]
  def addSuites(suites: Seq[TestCaseResult]): this.type
}

private final class TestStatisticsImpl extends TestStatistics {

  private var successCount: Int = 0
  private var failureCount: Int = 0
  private val suites: ArrayBuffer[TestCaseResult] = ArrayBuffer.empty
  private var deposited: Boolean = false

  override def toString: String =
    s"TestStatistics{${suitesReport(false)}; ${testsReport(false)}}".replaceAll("""\s""", "")

  override def incrementPassedTest(n: Int): Unit = {
    require(n >= 0)
    checkIfDeposited()
    successCount += n
  }
  override def incrementFailedTest(n: Int): Unit = {
    require(n >= 0)
    checkIfDeposited()
    failureCount += n
  }
  override def nextTestSuite(status: Status): Unit = {
    checkIfDeposited()
    val isSuccessful = status == Status.Success
    suites += TestCaseResult(failureCount, successCount, isSuccessful)
    successCount = 0
    failureCount = 0
  }

  override lazy val totalTestCnt: Int = {
    deposit()
    suites.map(_.total).sum
  }
  override lazy val passedTestCnt: Int = {
    deposit()
    suites.map(_.passed).sum
  }
  override lazy val failedTestCnt: Int = {
    deposit()
    suites.map(_.failed).sum
  }
  override lazy val totalSuiteCnt: Int = {
    deposit()
    suites.size
  }
  override lazy val passedSuiteCnt: Int = {
    deposit()
    suites.count(t => t.failed == 0 && t.isSuccessfulSuite)
  }
  override lazy val failedSuiteCnt: Int = {
    deposit()
    suites.count(t => t.failed > 0 || !t.isSuccessfulSuite)
  }

  private def deposit(): Unit = {
    val status = if (failureCount > 0) Status.Failure else Status.Success
    if (successCount + failureCount > 0) nextTestSuite(status)
    deposited = true
  }
  private def checkIfDeposited(): Unit = {
    if (deposited) throw new IllegalStateException("This TestStatistics has already been deposited.")
  }

  override def testsReport(ansi: Boolean): String = {
    val total = totalTestCnt + " total"
    val success = if (passedTestCnt > 0) green(ansi)(passedTestCnt + " passed") + ", " else ""
    val failure = if (failedTestCnt > 0) red(ansi)(failedTestCnt + " failed") + ", " else ""
    s"Test       : $failure$success$total"
  }
  override def suitesReport(ansi: Boolean): String = {
    val total = totalSuiteCnt + " total"
    val success = if (passedSuiteCnt > 0) green(ansi)(passedSuiteCnt + " passed") + ", " else ""
    val failure = if (failedSuiteCnt > 0) red(ansi)(failedSuiteCnt + " failed") + ", " else ""
    s"Test Suites: $failure$success$total"
  }
  private def red(ansi: Boolean)(in: String) = if (ansi) fansi.Color.Red(in) else in
  private def green(ansi: Boolean)(in: String) = if (ansi) fansi.Color.Green(in) else in

  override def getSuites: Seq[TestCaseResult] = this.suites.clone()
  override def addSuites(suites: Seq[TestCaseResult]): this.type = {
    checkIfDeposited()
    this.suites ++= suites
    this
  }
}