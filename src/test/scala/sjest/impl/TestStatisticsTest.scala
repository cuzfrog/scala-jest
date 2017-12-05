package sjest.impl

import utest._

import scala.util.Random

object TestStatisticsTest extends TestSuite {

  val tests = Tests {
    val impl = ModuleForTest().Prototype.testStatistics
    val suiteCnt = Random.nextInt(5) + 1
    'behavior {
      val suites = (1 to suiteCnt).map { _ =>
        val suite = new MockTestSuiteCnt
        suite.test(impl)
        impl.nextTestSuite()
        suite
      }

      val expectedTotalTestCnt = suites.map(_.totalCnt).sum
      assert(impl.totalTestCnt == expectedTotalTestCnt)

      val expectedSuccessfulTestCnt = suites.map(_.successfulTestCnt).sum
      assert(impl.passedTestCnt == expectedSuccessfulTestCnt)

      val expectedFailedTestCnt = suites.map(_.failedTestCnt).sum
      assert(impl.failedTestCnt == expectedFailedTestCnt)

      val expectedTotalSuiteCnt = suites.size
      assert(impl.totalSuiteCnt == expectedTotalSuiteCnt)

      val expectedSuccessfulSuiteCnt = suites.count(_.isFailed.unary_!)
      assert(impl.passedSuiteCnt == expectedSuccessfulSuiteCnt)

      val expectedFailedSuiteCnt = suites.count(_.isFailed)
      assert(impl.failedSuiteCnt == expectedFailedSuiteCnt)
    }
    'state {
      impl.passedSuiteCnt
      intercept[IllegalStateException]{
        impl.incrementFailedTest()
      }
    }
  }

  private class MockTestSuiteCnt {
    val successfulTestCnt: Int = Random.nextInt(30)
    val failedTestCnt: Int = Random.nextInt(3)
    val totalCnt: Int = successfulTestCnt + failedTestCnt

    def test(testStatistics: TestStatistics): Unit = {
      (1 to successfulTestCnt).foreach(_ => testStatistics.incrementPassedTest())
      (1 to failedTestCnt).foreach(_ => testStatistics.incrementFailedTest())
    }

    def isFailed: Boolean = failedTestCnt > 0
  }
}

