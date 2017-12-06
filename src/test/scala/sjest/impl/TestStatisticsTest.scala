package sjest.impl

import sjest.PropertyTest
import utest._

import scala.util.Random

object TestStatisticsTest extends TestSuite with PropertyTest {

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

      import impl._
      val expectedTotalTestCnt = suites.map(_.totalCnt).sum
      assert(totalTestCnt == expectedTotalTestCnt)

      val expectedPassedTestCnt = suites.map(_.passedTestCnt).sum
      assert(passedTestCnt == expectedPassedTestCnt)

      val expectedFailedTestCnt = suites.map(_.failedTestCnt).sum
      assert(failedTestCnt == expectedFailedTestCnt)

      val expectedTotalSuiteCnt = suites.size
      assert(totalSuiteCnt == expectedTotalSuiteCnt)

      val expectedPassedSuiteCnt = suites.count(_.isFailed.unary_!)
      assert(passedSuiteCnt == expectedPassedSuiteCnt)

      val expectedFailedSuiteCnt = suites.count(_.isFailed)
      assert(failedSuiteCnt == expectedFailedSuiteCnt)

      val suitesReport = impl.suitesReport(ansi = false)
      assert(
        suitesReport contains passedStr(expectedPassedSuiteCnt),
        suitesReport contains failedStr(expectedFailedSuiteCnt)
      )

      val testsReport = impl.testsReport(ansi = false)
      assert(
        testsReport contains passedStr(expectedPassedTestCnt),
        testsReport contains failedStr(expectedFailedTestCnt)
      )

      impl.report(ansi = false)
    }
    'state {
      impl.passedSuiteCnt
      intercept[IllegalStateException] {
        impl.incrementFailedTest()
      }
    }
    "addSuites" - {
      val mockSuites = (0 to Random.nextInt(100)).map(_ =>
        TestCaseResult(Random.nextInt(3), Random.nextInt(30))
      )
      val resultSuites = impl.addSuites(mockSuites).getSuites
      assert(resultSuites == mockSuites)
    }
  }

  private def passedStr(n: Int): String = if (n > 0) s"$n passed" else ""
  private def failedStr(n: Int): String = if (n > 0) s"$n failed" else ""

  private class MockTestSuiteCnt {
    val passedTestCnt: Int = Random.nextInt(30)
    val failedTestCnt: Int = Random.nextInt(3)
    val totalCnt: Int = passedTestCnt + failedTestCnt

    def test(testStatistics: TestStatistics): Unit = {
      if (passedTestCnt > 0) testStatistics.incrementPassedTest(passedTestCnt)
      if (failedTestCnt > 0) testStatistics.incrementFailedTest(failedTestCnt)
    }

    def isFailed: Boolean = failedTestCnt > 0
  }
}

