package sjest.impl

import org.scalajs.testinterface.TestUtils
import sbt.testing._
import sjest.{JestSuite, TestFrameworkConfig}

private[sjest] class JestTask(override val taskDef: TaskDef,
                              testClassLoader: ClassLoader)
                             (implicit config: TestFrameworkConfig) extends sbt.testing.Task {
  override def tags(): Array[String] = Array("jest-test-task")

  override def execute(eventHandler: EventHandler,
                       loggers: Array[Logger]): Array[Task] = {
    implicit val _taskDef: TaskDef = taskDef

    val suite = TestUtils.newInstance(taskDef.fullyQualifiedName(),
      testClassLoader, Seq.empty)(Seq.empty).asInstanceOf[JestSuite]

    val jsTestCase = suite.getTestCase(taskDef)
    val jsTestPath = JsTestConverter.generateJsTest(jsTestCase)

    val resultEvent =
      if (config.autoRunTestInSbt) NodejsTest().runTest(jsTestPath, loggers)
      else {
        loggers.foreach(_.info("---jest--- test.js files are generated," +
          " manually run the tests because auto run has been disabled"))
        JestTestEvent(Status.Success)
      }
    eventHandler.handle(resultEvent)
    Array.empty
  }

  override def execute(eventHandler: EventHandler,
                       loggers: Array[Logger],
                       continuation: Array[Task] => Unit): Unit = {
    this.execute(eventHandler, loggers)
    continuation(Array.empty)
  }
}
