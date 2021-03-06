package sjest.impl

import io.scalajs.nodejs.fs.Fs
import sbt.testing._
import sjest.support.{Utils, VisibleForTest}
import sjest.{JestSuite, TestFrameworkConfig}

import scala.util.{Failure, Success, Try}

private class JestTask(override val taskDef: TaskDef,
                       jsTestGenerator: JsTestGenerator,
                       nodejsTest: NodejsTest)
                      (implicit config: TestFrameworkConfig) extends sbt.testing.Task {
  override def tags(): Array[String] = Array("jest-test-task")

  override def execute(eventHandler: EventHandler,
                       loggers: Array[Logger]): Array[Task] = {
    val resultEvent = this.executeImpl(loggers)
    eventHandler.handle(resultEvent)
    Array.empty
  }

  override def execute(eventHandler: EventHandler,
                       loggers: Array[Logger],
                       continuation: Array[Task] => Unit): Unit = {
    this.execute(eventHandler, loggers)
    continuation(Array.empty)
  }

  @VisibleForTest
  private[sjest] def executeImpl(loggers: Array[Logger]): Event = {
    JestTask.validateConfig(config)

    implicit val _taskDef: TaskDef = taskDef

    val suite = JestTask.loadJestSuite(taskDef.fullyQualifiedName())
    suite.setSuiteName(taskDef.fullyQualifiedName())
    val jsTestPath = jsTestGenerator.generateJsTest(suite.container)

    if (config.argsConfig.autoRunTestInSbt) {
      nodejsTest.runTest(jsTestPath, loggers)
    }
    else {
      JestTestEvent(Status.Ignored)
    }
  }
}

private object JestTask {
  @throws[IllegalArgumentException]("when suite load fails.")
  private def loadJestSuite(fqcn: String): JestSuite = {
    Try {
      Utils.loadModule[JestSuite](fqcn)
    } match {
      case Success(suite) => suite
      case Failure(t) =>
        throw new IllegalArgumentException(s"Cannot load suite for name: $fqcn", t)
    }
  }

  @throws[IllegalArgumentException]
  private def validateConfig(config: TestFrameworkConfig): Unit = {
    require(Fs.existsSync(config.optJsPath), s"Cannot find ${config.optJsPath}")
  }
}