package sjest

import sbt.testing.{Task, TaskDef}
import sjest.impl.{ImplModule, JestTask}

private class JestRunner(override val args: Array[String],
                         override val remoteArgs: Array[String],
                         testClassLoader: ClassLoader)
                        (implicit config: TestFrameworkConfig) extends sbt.testing.Runner {

  private[this] val testStatistics = ImplModule.testStatistics

  override def tasks(taskDefs: Array[TaskDef]): Array[Task] =
    taskDefs.map(new JestTask(_, testClassLoader))

  override def done(): String = {
    testStatistics.suitesReport + NEWLINE +
      testStatistics.testsReport + NEWLINE
  }

  override def receiveMessage(msg: String): Option[String] = {
    //no message sent back to slave
    None
  }

  override def serializeTask(task: Task,
                             serializer: TaskDef => String): String = serializer(task.taskDef)

  override def deserializeTask(task: String, deserializer: String => TaskDef): Task = {
    new JestTask(deserializer(task), testClassLoader)
  }

}
