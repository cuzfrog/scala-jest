package sjest.impl

import sjest.support.Stateful

import scala.collection.mutable.ArrayBuffer

@Stateful
private sealed trait TestStatistics {
  def incrementPassedTest(n: Int = 1): Unit
  def incrementFailedTest(n: Int = 1): Unit
  def nextTestSuite(): Unit

  def totalTestCnt: Int
  def passedTestCnt: Int
  def failedTestCnt: Int

  def totalSuiteCnt: Int
  def passedSuiteCnt: Int
  def failedSuiteCnt: Int

  def isAllPassed: Boolean = totalSuiteCnt == passedSuiteCnt

  def testsReport: String
  def suitesReport: String
  def report: String = suitesReport + NEWLINE + testsReport
}

private final class TestStatisticsImpl extends TestStatistics {

  private var successCount: Int = 0
  private var failureCount: Int = 0
  private val suites: ArrayBuffer[(Int, Int)] = ArrayBuffer.empty
  private var deposited: Boolean = false

  override def incrementPassedTest(n: Int): Unit = {
    checkIfDeposited()
    successCount += n
  }
  override def incrementFailedTest(n: Int): Unit = {
    checkIfDeposited()
    failureCount += n
  }
  override def nextTestSuite(): Unit = {
    checkIfDeposited()
    suites += (successCount -> failureCount)
    successCount = 0
    failureCount = 0
  }

  override lazy val totalTestCnt: Int = {
    deposit()
    suites.map { case (s, f) => s + f }.reduceOption(_ + _).getOrElse(0)
  }
  override lazy val passedTestCnt: Int = {
    deposit()
    suites.map { case (s, _) => s }.reduceOption(_ + _).getOrElse(0)
  }
  override lazy val failedTestCnt: Int = {
    deposit()
    suites.map { case (_, f) => f }.reduceOption(_ + _).getOrElse(0)
  }
  override lazy val totalSuiteCnt: Int = {
    deposit()
    suites.size
  }
  override lazy val passedSuiteCnt: Int = {
    deposit()
    suites.count { case (_, f) => f == 0 }
  }
  override lazy val failedSuiteCnt: Int = {
    deposit()
    suites.count { case (_, f) => f > 0 }
  }

  private def deposit(): Unit = {
    if (successCount + failureCount > 0) nextTestSuite()
    deposited = true
  }
  private def checkIfDeposited(): Unit = {
    if (deposited) throw new IllegalStateException("This TestStatistics has already been deposited.")
  }

  override def testsReport: String = {
    val total = totalTestCnt + " total"
    val success = if (passedTestCnt > 0) fansi.Color.Green(passedTestCnt + " passed, ") else ""
    val failure = if (failedTestCnt > 0) fansi.Color.Red(failedTestCnt + " failed, ") else ""
    s"Test       : $failure$success$total"
  }
  override def suitesReport: String = {
    val total = totalSuiteCnt + " total"
    val success = if (passedSuiteCnt > 0) fansi.Color.Green(passedSuiteCnt + " passed, ") else ""
    val failure = if (failedSuiteCnt > 0) fansi.Color.Red(failedSuiteCnt + " failed, ") else ""
    s"Test Suites: $failure$success$total"
  }
}