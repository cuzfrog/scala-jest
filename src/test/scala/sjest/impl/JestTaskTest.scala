package sjest.impl

import io.scalajs.nodejs.fs.Fs
import io.scalajs.nodejs.path.Path
import sbt.testing.Status
import utest._
import sjest.MockObjects

import scala.concurrent.Future

object JestTaskTest extends TestSuite {

  import MockObjects.mockSuccessTestFqcn

  private val config = MockObjects.mockConfig
    .copy(optJsPath = "target/scala-2.12/sjest-test-fastopt.js")

  val tests = Tests {
    val logger = new TestLogger
    "no-auto-run" - {
      implicit val noRunConfig = config.copy(autoRunTestInSbt = false)
      val task = ModuleForTest().Prototype.jestTask(mockSuccessTestFqcn)
      val event = task.executeImpl(Array(logger))

      val testFile = Path.resolve(config.testJsDir, mockSuccessTestFqcn + ".test.js")
      assert(Fs.existsSync(testFile))
      assert(event.status() == Status.Ignored)
    }
    "success" - {
      implicit val goodConfig = config
      val task = ModuleForTest().Prototype.jestTask(mockSuccessTestFqcn)
      import scalajs.concurrent.JSExecutionContext.Implicits.queue
      Future {
        val event = task.executeImpl(Array(logger))
        if (event.status != Status.Success) logger.flush()
        assert(event.status == Status.Success)
      }
    }
    "bad-opt.js-path" - {
      implicit val badConfig = config.copy(optJsPath = "some.bad.path.opt.js")
      val task = ModuleForTest().Prototype.jestTask(mockSuccessTestFqcn)
      val exception = intercept[IllegalArgumentException] {
        task.executeImpl(Array(logger))
      }
      assert(exception.getMessage contains "Cannot find some.bad.path.opt.js")
    }
    "bad-suite-name" - {
      implicit val goodConfig = config
      val fqcn = "some.bad.test.class.name"
      val task = ModuleForTest().Prototype.jestTask(fqcn)
      val exception = intercept[IllegalArgumentException] {
        task.executeImpl(Array(logger))
      }
      assert(exception.getMessage contains s"Cannot load suite for name: $fqcn")
    }
  }
}
