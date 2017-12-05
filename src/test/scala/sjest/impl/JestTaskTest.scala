package sjest.impl

import io.scalajs.nodejs.fs.Fs
import io.scalajs.nodejs.path.Path
import sbt.testing.Status
import utest._
import sjest.MockObjects

object JestTaskTest extends TestSuite {

  import MockObjects.mockSuccessTestFqcn

  val tests = Tests {
    val logger = new TestLogger
    "no-auto-run" - {
      implicit val config = MockObjects.mockConfig.copy(autoRunTestInSbt = false)
      val task = ModuleForTest().Prototype.jestTask(mockSuccessTestFqcn)
      val event = task.executeImpl(Array(logger))

      val testFile = Path.resolve(config.testJsDir, mockSuccessTestFqcn + ".test.js")
      assert(Fs.existsSync(testFile))
      assert(event.status() == Status.Ignored)
    }
    "auto-run-success" - {
      implicit val config = MockObjects.mockConfig
      val task = ModuleForTest().Prototype.jestTask(mockSuccessTestFqcn)
      val event = task.executeImpl(Array(logger))
      if(event.status != Status.Success) logger.flush()
      assert(event.status == Status.Success)
    }
  }
}
