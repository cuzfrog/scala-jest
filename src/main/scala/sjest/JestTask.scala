package sjest

import nodejs.ChildProcess
import org.scalajs.testinterface.TestUtils
import sbt.testing._

import scala.concurrent.duration.Deadline
import scala.util.control.NonFatal

private class JestTask(override val taskDef: TaskDef,
                     testClassLoader: ClassLoader) extends sbt.testing.Task {
  override def tags(): Array[String] = Array("my-test-task")

  override def execute(eventHandler: EventHandler,
                       loggers: Array[Logger]): Array[Task] = {
    implicit val _taskDef: TaskDef = taskDef

    loggers.foreach(_.info(s"testing against: ${taskDef.fullyQualifiedName}"))
    val suite =
      TestUtils.loadModule(taskDef.fullyQualifiedName, testClassLoader).asInstanceOf[JestSuite]

    val jsTestCase = suite.getTestCase(taskDef)
    JsTestConverter.generateJsTests(jsTestCase)

    val startTime = Deadline.now
    val event = try {
      ChildProcess.execSync(s"npm test -- ${jsTestCase.getPath}") //run code with nodejs
      JestTestEvent(Status.Success)
    } catch {
      case NonFatal(t) =>
        loggers.foreach(_.error(s"test failed with $t}"))
        JestTestEvent(Status.Failure)
    }

    val duration = (Deadline.now - startTime).toMillis
    eventHandler.handle(event.copy(duration = duration): Event)

    Array.empty
  }

  override def execute(eventHandler: EventHandler,
                       loggers: Array[Logger],
                       continuation: Array[Task] => Unit): Unit = {
    this.execute(eventHandler, loggers)
    continuation(Array.empty)
  }
}
