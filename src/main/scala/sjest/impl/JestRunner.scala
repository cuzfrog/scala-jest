package sjest.impl

import sbt.testing.{Task, TaskDef}
import sjest.impl.JestRunner.{Args, RemoteArgs}

import scala.scalajs.js

private class JestRunner(_args: Args,
                         _remoteArgs: RemoteArgs,
                         taskFactory: TaskDef => Task,
                         testStatistics: TestStatistics) extends sbt.testing.Runner {

  override val args: Array[String] = _args.args
  override val remoteArgs: Array[String] = _remoteArgs.args

  override def tasks(taskDefs: Array[TaskDef]): Array[Task] = taskDefs.map(taskFactory)

  override def done(): String = testStatistics.report()

  override def receiveMessage(msg: String): Option[String] = {
    val suites = js.JSON.parse(msg).asInstanceOf[js.Array[String]].map(TestCaseResult.fromJson)
    testStatistics.addSuites(suites)
    //no message sent back to slave
    None
  }

  override def serializeTask(task: Task,
                             serializer: TaskDef => String): String = serializer(task.taskDef)

  override def deserializeTask(task: String, deserializer: String => TaskDef): Task = {
    taskFactory(deserializer(task))
  }
}

private class JestSlaveRunner(_args: Args,
                              _remoteArgs: RemoteArgs,
                              taskFactory: TaskDef => Task,
                              testStatistics: TestStatistics,
                              communicationTunnel: String => Unit)
  extends JestRunner(_args, _remoteArgs, taskFactory, testStatistics) {
  override def done(): String = {
    import scalajs.js.JSConverters._
    val suites = testStatistics.getSuites.map(_.toJson).toJSArray
    communicationTunnel(js.JSON.stringify(suites))
    ""
  }
}

private object JestRunner {
  class Args(val args: Array[String]) extends JestRunnerArgs
  class RemoteArgs(val args: Array[String]) extends JestRunnerArgs
}

private sealed trait JestRunnerArgs {

  import js.JSConverters._

  def args: Array[String]
  def combine(more: js.Array[String]): js.Array[String] = {
    (args.toJSArray ++ more).distinct
  }
}