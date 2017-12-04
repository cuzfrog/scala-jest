package sjest.impl

import io.scalajs.nodejs.path.Path
import sbt.testing.{Logger, Status, TaskDef}
import sjest.JestFramework.NodejsCmd
import sjest.nodejs.FSUtils
import sjest.{MockObjects, TestFrameworkConfig}
import utest._

import scala.scalajs.js
import scala.util.Random

object NodejsTestImplTest extends TestSuite {


  private implicit val mockConfig: TestFrameworkConfig = MockObjects.mockConfig
  private val impl: NodejsTestImpl = ModuleForTest().Prototype.nodejsTestImpl
  private implicit val taskDef: TaskDef = MockObjects.newTaskDef("test-taskDef")


  val tests = Tests {
    val logger = new TestLogger
    "successful-test" - {
      val mockJsTestFile = new MockJsTestFile
      val event = impl.runTest(mockJsTestFile.testPath, Array(logger))
      if(event.status() != Status.Success) logger.flush()
      assert(event.status() == Status.Success)
    }
    "failed-test" - {
      val mockJsTestFile = new MockJsTestFileFailed
      val event = impl.runTest(mockJsTestFile.testPath, Array.empty)
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