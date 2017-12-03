package sjest.impl

import scala.collection.mutable.ArrayBuffer

private[sjest] sealed trait TestStatistics {
  def incrementSuccessTest(): Unit
  def incrementFailureTest(): Unit
  def nextTestSuite(): Unit

  def totalTestCnt: Int
  def successfulTestCnt: Int
  def failedTestCnt: Int

  def totalSuiteCnt: Int
  def successfulSuiteCnt: Int
  def failedSuiteCnt: Int
}

private final class TestStatisticsImpl extends TestStatistics {

  private var successCount: Int = 0
  private var failureCount: Int = 0
  private val suites: ArrayBuffer[(Int, Int)] = ArrayBuffer.empty

  override def incrementSuccessTest(): Unit = successCount += 1
  override def incrementFailureTest(): Unit = failureCount += 1
  override def nextTestSuite(): Unit = {
    suites += (successCount -> failureCount)
    successCount = 0
    failureCount = 0
  }

  override def totalTestCnt: Int = {
    deposit()
    suites.map { case (s, f) => s + f }.reduceOption(_ + _).getOrElse(0)
  }
  override def successfulTestCnt: Int = {
    deposit()
    suites.map { case (s, _) => s }.reduceOption(_ + _).getOrElse(0)
  }
  override def failedTestCnt: Int = {
    deposit()
    suites.map { case (_, f) => f }.reduceOption(_ + _).getOrElse(0)
  }
  override def totalSuiteCnt: Int = {
    deposit()
    suites.size
  }
  override def successfulSuiteCnt: Int = {
    deposit()
    suites.count { case (_, f) => f == 0 }
  }
  override def failedSuiteCnt: Int = {
    deposit()
    suites.count { case (_, f) => f > 0 }
  }

  private def deposit(): Unit = if (successCount + failureCount > 0) nextTestSuite()
}