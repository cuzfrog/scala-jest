package sjest.impl

import io.scalajs.nodejs.path.Path
import sbt.testing.{Status, TaskDef}
import sjest.nodejs.FSUtils
import sjest.{AddTestUtilities, MockObjects, TestFrameworkConfig}
import utest._

import scala.concurrent.Future
import scala.util.Random

import scalajs.concurrent.JSExecutionContext.Implicits.queue

object NodejsTestImplTest extends sjest.BaseSuite {

  private implicit val mockConfig: TestFrameworkConfig = MockObjects.mockConfig
  private implicit val taskDef: TaskDef = MockObjects.newTaskDef("test-taskDef")
  private val module = ModuleForTest()

  //override def utestAfterAll(): Unit = FSUtils.delete(mockConfig.testJsDir)

  val tests = Tests {
    val logger = new TestLogger
    'unit - {
      val impl: NodejsTestImpl = module.Prototype.nodejsTestImpl
      "successful-test" - Future{
        val mockJsTestFile = new MockJsTestFile
        val event = impl.runTest(mockJsTestFile.testPath, Array(logger))
        if (event.status() != Status.Success) logger.flush()
        assert(event.status() == Status.Success)

        val printMsg = logger.getAndEmptyContent.find(_.contains(mockJsTestFile.printMessageOnPass))
        if (mockJsTestFile.config.silentOnPass) assert(printMsg.isEmpty)
        else assert(printMsg.isDefined)
      }
      "silentOnPass-false" - Future{
        val mockJsTestFile = new MockJsTestFile
        val module = ModuleForTest()(mockConfig.copy(silentOnPass = false))
        val impl = module.Prototype.nodejsTestImpl
        impl.runTest(mockJsTestFile.testPath, Array(logger))
        val output = logger.getAndEmptyContent
        val printMsg = output.find(_.contains(mockJsTestFile.printMessageOnPass))
        assert(printMsg.isDefined)
      }
      "failed-throw-test" - Future{
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
                            (implicit val config: TestFrameworkConfig) extends AddTestUtilities {
  require(failed + passed > 0 && failed >= 0 && passed >= 0)
  val testName: String = "mock-test-" + Random.genAlphanumeric(10)
  val testDescription: String = Random.genAlphanumeric(20)

  val relativeOptJsPath: String = Path.relative(config.testJsDir, config.optJsPath)

  val jsTestContent: String = (genFailed ++ genPassed).mkString(NEWLINE)

  lazy val testPath: String = {
    FSUtils.write(Path.resolve(config.testJsDir, testName + ".test.js"), jsTestContent)
  }

  lazy val printMessageOnPass: String = Random.genAlphanumeric(20)

  def genFailed: Seq[String] = (1 to failed).map { idx =>
    s"""test('$testDescription-failed-$idx', () => {
       |  expect(1 + 2).toBe(0);
       |});
       |""".stripMargin
  }
  def genPassed: Seq[String] = (1 to passed).map { idx =>
    s"""test('$testDescription-passed-$idx', () => {
       |  expect(1 + 2).toBe(3);
       |  console.log('$printMessageOnPass')
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