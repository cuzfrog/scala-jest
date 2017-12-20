package sjest.impl

import sbt.testing.{Task, TaskDef}
import sjest.impl.JestRunner.{Args, RemoteArgs}
import sjest.{GlobalStub, TestFrameworkConfig}

import scala.scalajs.js
import scala.scalajs.js.JSON

private class JestRunner(_args: Args,
                         taskFactory: TaskDef => Task,
                         testStatistics: TestStatistics) extends sbt.testing.Runner {

  override val args: Array[String] = _args.args
  override val remoteArgs: Array[String] = Array.empty

  override def tasks(taskDefs: Array[TaskDef]): Array[Task] = taskDefs.map(taskFactory)

  override def done(): String = {
    testStatistics.report()
  }

  override def receiveMessage(msg: String): Option[String] = {
    val runnerMessage = JSON.parse(msg).asInstanceOf[RunnerMessage]
    runnerMessage.tpe match {
      case RunnerMessage.Type.TestStatistics =>
        val suites = runnerMessage.payload.asInstanceOf[js.Array[String]].map(TestCaseResult.fromJson)
        testStatistics.addSuites(suites)
      case _ =>
        throw new UnsupportedOperationException(s"Master received: ${runnerMessage.payload}")
    }

    //no message sent back to slave
    None
  }

  override def serializeTask(task: Task,
                             serializer: TaskDef => String): String = serializer(task.taskDef)

  override def deserializeTask(task: String, deserializer: String => TaskDef): Task = {
    taskFactory(deserializer(task))
  }
}

private class JestSlaveRunner[S](_args: Args,
                                 taskFactory: TaskDef => Task,
                                 testStatistics: TestStatistics,
                                 globalStub: GlobalStub[S],
                                 sendToMaster: String => Unit)
                                (implicit config: TestFrameworkConfig)
  extends JestRunner(_args, taskFactory, testStatistics) {

  override def done(): String = {
    import scalajs.js.JSConverters._
    val suites = testStatistics.getSuites.map(_.toJson).toJSArray
    sendToMaster(JSON.stringify(new RunnerMessage(RunnerMessage.Type.TestStatistics, suites)))
    globalStub.globalTeardown(globalStub.setupResult)
    ""
  }
}

private object JestRunner {
  class Args(val args: Array[String]) extends JestRunnerArgs
  class RemoteArgs(val args: Array[String]) extends JestRunnerArgs
}

private class RunnerMessage(val tpe: String, val payload: js.Object) extends js.Object
private object RunnerMessage {
  object Type {
    val TestStatistics: String = "TestStatistics"
  }
}

private sealed trait JestRunnerArgs {

  import js.JSConverters._

  def args: Array[String]
  def combine(more: js.Array[String]): js.Array[String] = {
    (args.toJSArray ++ more).distinct
  }
}