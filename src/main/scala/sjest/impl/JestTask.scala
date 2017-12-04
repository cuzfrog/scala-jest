package sjest.impl

import org.scalajs.testinterface.TestUtils
import sbt.testing._
import sjest.support.VisibleForTest
import sjest.{JestSuite, TestFrameworkConfig}

private class JestTask(override val taskDef: TaskDef,
                       testClassLoader: ClassLoader,
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
    implicit val _taskDef: TaskDef = taskDef

    val suite = TestUtils.newInstance(taskDef.fullyQualifiedName(),
      testClassLoader, Seq.empty)(Seq.empty).asInstanceOf[JestSuite]

    val jsTestCase = suite.getTestCase(taskDef)
    val jsTestPath = jsTestConverter.generateJsTest(jsTestCase)

    val event = if (config.autoRunTestInSbt)
      nodejsTest.runTest(jsTestPath, loggers)
    else {
      loggers.foreach(_.info("*.test.js files are generated," +
        " manually run the tests because auto run has been disabled"))
      JestTestEvent(Status.Ignored)
    }
    event
  }
}
