package sjest.impl

import sbt.testing.{Event, Logger, Status, TaskDef}
import sjest.{JestFramework, TestFrameworkConfig}
import sjest.nodejs.{ChildProcess, ChildProcessOpt, FSUtils}

import scala.concurrent.duration.Deadline
import scala.util.control.NonFatal

import sjest.LoggersOps

private[sjest] sealed trait NodejsTest {
  def runTest(jsTestPath: String, loggers: Array[Logger])
             (implicit taskDef: TaskDef, config: TestFrameworkConfig): Event
}

private class NodejsTestImpl extends NodejsTest {

  def runTest(jsTestPath: String, loggers: Array[Logger])
             (implicit taskDef: TaskDef, config: TestFrameworkConfig): Event = {
    val startTime = Deadline.now
    loggers.info(s"Testing ${fansi.Bold.On(taskDef.fullyQualifiedName)}")
    val event = try {

      val JestFramework.NodejsCmd(cmd, args) = config.nodejsCmdOfPath(jsTestPath)
      val childProcess = ChildProcess.spawnSync(cmd, args) //run code with nodejs

      this.resolveChildProcess(childProcess, loggers)
    } catch {
      case NonFatal(t) =>
        loggers.error(s"Test failed with ${fansi.Color.Red(t.toString)}")
        JestTestEvent(Status.Failure)
    }

    val duration = (Deadline.now - startTime).toMillis
    event.copy(duration = duration)
  }

  private def resolveChildProcess(childProcess: ChildProcessOpt, loggers: Array[Logger])
                                 (implicit taskDef: TaskDef, config: TestFrameworkConfig): JestTestEvent = {
    childProcess.status match {
      case 0 =>
        val output = childProcess.outputOpt.map(config.jestOutputFilter)
        output.foreach(loggers.info)
        JestTestEvent(Status.Success)
      case _ =>
        val output = childProcess.stderrOpt.map(config.jestOutputFilter)
        output.foreach(loggers.error)
        JestTestEvent(Status.Failure)
    }
  }


}