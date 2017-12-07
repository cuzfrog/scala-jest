package sjest.impl

import io.scalajs.nodejs.fs.Fs
import sbt.testing._
import sjest.support.VisibleForTest
import sjest.{JestSuite, TestFrameworkConfig}

import scala.scalajs.reflect.Reflect
import scala.util.{Failure, Success, Try}

private class JestTask(override val taskDef: TaskDef,
                       jsTestConverter: JsTestConverter,
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

    val jsTestContainer = suite.getTestCase(taskDef)
    val jsTestPath = jsTestConverter.generateJsTest(jsTestContainer)

    val event = if (config.autoRunTestInSbt) {
      nodejsTest.runTest(jsTestPath, loggers)
    }
    else {
      loggers.foreach(_.info("*.test.js files are generated," +
        " manually run the tests because auto run has been disabled"))
      JestTestEvent(Status.Ignored)
    }
    event
  }
}

private object JestTask {
  @throws[IllegalArgumentException]("when suite load fails.")
  private def loadJestSuite(fqcn: String): JestSuite = {
    Try {
      Reflect.lookupInstantiatableClass(fqcn) match {
        case Some(clazz) => clazz.newInstance().asInstanceOf[JestSuite]
        case None => throw new ClassNotFoundException(fqcn)
      }
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