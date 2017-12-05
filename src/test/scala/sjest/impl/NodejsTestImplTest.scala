package sjest.impl

import io.scalajs.nodejs.path.Path
import sbt.testing.{Status, TaskDef}
import sjest.nodejs.FSUtils
import sjest.{MockObjects, TestFrameworkConfig}
import utest._

import scala.util.Random

object NodejsTestImplTest extends TestSuite {


  private implicit val mockConfig: TestFrameworkConfig = MockObjects.mockConfig
  private implicit val taskDef: TaskDef = MockObjects.newTaskDef("test-taskDef")
  private val module = ModuleForTest()

  val tests = Tests {
    val logger = new TestLogger
    'unit - {
      val impl: NodejsTestImpl = module.Prototype.nodejsTestImpl
      "successful-test" - {
        val mockJsTestFile = new MockJsTestFile
        val event = impl.runTest(mockJsTestFile.testPath, Array(logger))
        if (event.status() != Status.Success) logger.flush()
        assert(event.status() == Status.Success)
      }
      "failed-throw-test" - {
        val mockJsTestFile = new MockJsTestFileFailedThrow
        val event = impl.runTest(mockJsTestFile.testPath, Array(logger))
        assert(event.status() == Status.Failure)
      }
    }
    "integrated" - {
      val impl: NodejsTestImpl = module.Singleton.nodejsTestImpl
      val testStatistics = module.Singleton.testStatistics
      "testStatistics" - {
        val suites = Seq(
          new MockJsTestFile(5, 4),
          new MockJsTestFile(0, 15),
          new MockJsTestFile(0, 11),
          new MockJsTestFileFailedThrow
        )
        suites.foreach(s => impl.runTest(s.testPath, Array(logger)))
        import testStatistics._
        assert(
          totalSuiteCnt == suites.length,
          failedSuiteCnt == suites.count(_.failed > 0),
          passedSuiteCnt == suites.count(_.failed == 0),
          totalTestCnt == suites.map(s => s.failed + s.passed).sum,
          failedTestCnt == suites.map(_.failed).sum,
          passedTestCnt == suites.map(_.passed).sum
        )
      }
    }
  }
}

private class MockJsTestFile(val failed: Int = 0, val passed: Int = 1)
                            (implicit config: TestFrameworkConfig) {
  require(failed + passed > 0 && failed >= 0 && passed >= 0)
  val testName: String = "mock-test-" + Random.genAlphanumeric(10)
  val testDescription: String = Random.genAlphanumeric(20)

  val relativeOptJsPath: String = Path.relative(config.testJsDir, config.optJsPath)

  val jsTestContent: String = (genFailed ++ genPassed).mkString(NEWLINE)

  lazy val testPath: String = {
    FSUtils.write(Path.resolve(config.testJsDir, testName + ".test.js"), jsTestContent)
  }

  def genFailed: Seq[String] = (1 to failed).map { idx =>
    s"""test('$testDescription-failed-$idx', () => {
       |  expect(1 + 2).toBe(0);
       |});
       |""".stripMargin
  }
  def genPassed: Seq[String] = (1 to passed).map { idx =>
    s"""test('$testDescription-passed-$idx', () => {
       |  expect(1 + 2).toBe(3);
       |});
       |""".stripMargin
  }
}

private class MockJsTestFileFailedThrow(implicit config: TestFrameworkConfig)
  extends MockJsTestFile(1, 0) {
  override val jsTestContent: String =
    s"""test('$testDescription', () => {
       |  throw 'failed test with throwing';
       |});""".stripMargin
}