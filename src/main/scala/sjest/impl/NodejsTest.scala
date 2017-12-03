package sjest.impl

import sbt.testing.{Event, Logger, Status, TaskDef}
import sjest.{JestFramework, TestFrameworkConfig}
import sjest.nodejs.{ChildProcess, ChildProcessOpt, FSUtils}

import scala.concurrent.duration.Deadline
import scala.util.control.NonFatal
import sjest.LoggersOps

private[sjest] sealed trait NodejsTest {
  def runTest(jsTestPath: String, loggers: Array[Logger], testStatistics: TestStatistics)
             (implicit taskDef: TaskDef, config: TestFrameworkConfig): Event
}

private class NodejsTestImpl(jestOutputParser: JestOutputParser) extends NodejsTest {

  def runTest(jsTestPath: String, loggers: Array[Logger], testStatistics: TestStatistics)
             (implicit taskDef: TaskDef, config: TestFrameworkConfig): Event = {
    val startTime = Deadline.now
    loggers.info(s"Testing ${fansi.Bold.On(taskDef.fullyQualifiedName)}")
    val event = try {

      val JestFramework.NodejsCmd(cmd, args) = config.nodejsCmdOfPath(jsTestPath)
      val childProcess = ChildProcess.spawnSync(cmd, args) //run code with nodejs

      this.resolveChildProcess(childProcess, loggers, testStatistics)
    } catch {
      case NonFatal(t) =>
        loggers.error(s"Test failed with ${fansi.Color.Red(t.toString)}")
        JestTestEvent(Status.Failure)
    }

    val duration = (Deadline.now - startTime).toMillis
    event.copy(duration = duration)
  }

  private def resolveChildProcess(childProcess: ChildProcessOpt,
                                  loggers: Array[Logger],
                                  testStatistics: TestStatistics)
                                 (implicit taskDef: TaskDef, config: TestFrameworkConfig): JestTestEvent = {
    val (status, outputOpt) = childProcess.status match {
      case 0 =>
        val output = childProcess.outputOpt
        output.map(config.jestOutputFilter).foreach(loggers.info)
        (Status.Success, output)
      case _ =>
        val output = childProcess.stderrOpt
        output.map(config.jestOutputFilter).foreach(loggers.error)
        (Status.Failure, output)
    }
    outputOpt.foreach(output => jestOutputParser.extractStatistics(output, testStatistics))
    JestTestEvent(status)
  }

}