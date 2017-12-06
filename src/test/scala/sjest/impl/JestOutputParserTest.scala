package sjest.impl

import utest._

object JestOutputParserTest extends TestSuite {

  private val impl = ModuleForTest().Prototype.jestOutputParser

  val tests = Tests {
    "positive1-passed" - {
      val mockOutput =
        """ PASS  tests/target/sjests/anywhere.Test2.test.js
          |  ✓ this is my second test (19ms)
          |
          |  console.log tests/target/scala-2.12/sjest-tests-test-fastopt.js:30557
          |    do some test2!
          |
          |Test Suites: 1 passed, 1 total
          |Tests:       3 passed, 3 total
          |Snapshots:   0 total
          |Time:        0.645s, estimated 1s
          |Ran all test suites matching /tests\/target\/sjests\/anywhere.Test2.test.js/i.
          |""".stripMargin

      val result = impl.extractStatistics(mockOutput)
      val expected = TestCaseResult(0, 3)
      assert(result == expected)
    }
    "positive-failed+passed" - {
      val mockOutput =
        """Test Suites: 1 failed, 1 total
          |Tests:       15 failed, 1 passed, 2 total
          |Snapshots:   0 total
          |""".stripMargin
      val result = impl.extractStatistics(mockOutput)
      val expected = TestCaseResult(15, 1)
      assert(result == expected)
    }
    "positive-failed" - {
      val mockOutput =
        """Test Suites: 1 failed, 1 of 2 total
          |Tests:       15 failed, 15 of 23 total
          |""".stripMargin
      val result = impl.extractStatistics(mockOutput)
      val expected = TestCaseResult(15, 0)
      assert(result == expected)
    }
    "negative-bad-format" - {
      val mockOutput =
        """Test Suites: 1 failed, 1 total
          |Tests:  bad format
          |""".stripMargin
      intercept[IllegalArgumentException] {
        impl.extractStatistics(mockOutput)
      }
    }
    "negative-empty" - {
      val mockOutput = ""
      intercept[IllegalArgumentException] {
        impl.extractStatistics(mockOutput)
      }
    }
  }
}
