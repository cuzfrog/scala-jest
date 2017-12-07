package sjest.impl

import sbt.testing.{Event, Logger, Status, TaskDef}
import sjest.nodejs.{ChildProcess, ChildProcessOpt}
import sjest.support.SideEffect
import sjest.{JestFramework, TestFrameworkConfig}

import scala.concurrent.duration.Deadline
import scala.scalajs.js
import scala.util.control.NonFatal
import sjest.NEWLINE

private sealed trait NodejsTest {
  def runTest(jsTestPath: String, loggers: Array[Logger])(implicit taskDef: TaskDef): Event
}

private class NodejsTestImpl(jestOutputParser: JestOutputParser,
                             val testStatistics: TestStatistics)
                            (implicit config: TestFrameworkConfig) extends NodejsTest {

  @SideEffect(this.testStatistics)
  def runTest(jsTestPath: String, loggers: Array[Logger])(implicit taskDef: TaskDef): Event = {
    val startTime = Deadline.now
    loggers.info(s"Testing ${fansi.Bold.On(taskDef.fullyQualifiedName)}")
    val event = try {

      val JestFramework.NodejsCmd(cmd, args) = config.nodejsCmdOfPath(jsTestPath)
      val childProcess = ChildProcess.spawnSync(cmd, args) //run code with nodejs

      val testEvent = this.resolveChildProcess(childProcess, loggers)
      testStatistics.nextTestSuite()
      testEvent
    } catch {
      case NonFatal(t) =>
        loggers.error(s"Test failed: ${fansi.Color.Red(t.toString)}")
        loggers.error(t.getStackTrace.mkString(NEWLINE))
        JestTestEvent(Status.Failure)
    }

    val duration = (Deadline.now - startTime).toMillis
    event.copy(duration = duration)
  }

  @SideEffect(testStatistics)
  private def resolveChildProcess(childProcess: ChildProcessOpt,
                                  loggers: Array[Logger])
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
    outputOpt.foreach(this.jestOutputParser andThen this.countTestResult)
    JestTestEvent(status)
  }

  @SideEffect(testStatistics)
  private def countTestResult(testCaseResult: TestCaseResult): Unit = {
    testStatistics.incrementPassedTest(testCaseResult.passed)
    testStatistics.incrementFailedTest(testCaseResult.failed)
  }
}