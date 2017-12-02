package sjest

import io.scalajs.nodejs.path.Path
import sbt.testing.{Logger, Status, TaskDef}
import sjest.JestFramework.NodejsCmd
import sjest.nodejs.FSUtils
import utest._

import scala.scalajs.js
import scala.util.Random

object NodejsTestImplTest extends TestSuite {

  private implicit val mockConfig: TestFrameworkConfig = MockObjects.mockConfig.copy(
    nodejsCmdOfPath = (jsTestPath: String) => NodejsCmd("npm", js.Array("test", "--", jsTestPath))
  )
  private implicit val taskDef: TaskDef = MockObjects.newTaskDef("test-taskDef")

  private val logger = new Logger {
    override def ansiCodesSupported(): Boolean = false
    override def debug(msg: String): Unit = ???
    override def error(msg: String): Unit = println("[error]" + msg)
    override def warn(msg: String): Unit = ???
    override def trace(t: Throwable): Unit = ???
    override def info(msg: String): Unit = println("[info ]" + msg)
  }

  val tests = Tests {
    "successful-test" - {
      val mockJsTestFile = new MockJsTestFile
      val event = NodejsTestImpl.runTest(mockJsTestFile.testPath, Array.empty)
      assert(event.status() == Status.Success)
    }
    "failed-test" - {
      val mockJsTestFile = new MockJsTestFileFailed
      val event = NodejsTestImpl.runTest(mockJsTestFile.testPath, Array.empty)
      assert(event.status() == Status.Failure)
    }
  }
}

private class MockJsTestFile(implicit config: TestFrameworkConfig) {
  val testName: String = "mock-test-" + Random.genAlphanumeric(10)
  val testDescription: String = Random.genAlphanumeric(20)

  val relativeOptJsPath: String = Path.relative(config.testJsDir, config.optJsPath)

  val jsTestContent: String =
    s"""test('$testDescription', () => {
       |  expect(1 + 2).toBe(3);
       |});""".stripMargin

  lazy val testPath: String = {
    FSUtils.write(Path.resolve(config.testJsDir, testName + ".test.js"), jsTestContent)
  }
}

private class MockJsTestFileFailed(implicit config: TestFrameworkConfig) extends MockJsTestFile {
  override val jsTestContent: String =
    s"""test('$testDescription', () => {
       |  expect(1 + 6).toBe(3);
       |});""".stripMargin
}