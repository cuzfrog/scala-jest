package sjest.impl

import sbt.testing.{Task, TaskDef}
import sjest.impl.JestRunner.{Args, RemoteArgs}

private class JestRunner(_args: Args,
                         _remoteArgs: RemoteArgs,
                         taskFactory: TaskDef => Task,
                         testStatistics: TestStatistics) extends sbt.testing.Runner {

  override val args: Array[String] = _args.args
  override val remoteArgs: Array[String] = _remoteArgs.args

  override def tasks(taskDefs: Array[TaskDef]): Array[Task] = taskDefs.map(taskFactory)

  override def done(): String = testStatistics.report

  override def receiveMessage(msg: String): Option[String] = {
    //no message sent back to slave
    None
  }

  override def serializeTask(task: Task,
                             serializer: TaskDef => String): String = serializer(task.taskDef)

  override def deserializeTask(task: String, deserializer: String => TaskDef): Task = {
    taskFactory(deserializer(task))
  }
}

private object JestRunner {
  class Args(val args: Array[String]) extends AnyVal
  class RemoteArgs(val args: Array[String]) extends AnyVal
}