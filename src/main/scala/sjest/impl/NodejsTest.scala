package sjest.impl

import sbt.testing.{Event, Logger, Status, TaskDef}
import sjest.impl.JestRunner.Args
import sjest.nodejs.{ChildProcess, ChildProcessOpt}
import sjest.support.SideEffect
import sjest.{JestFramework, TestFrameworkConfig}

import scala.concurrent.duration.Deadline
import scala.util.control.NonFatal

private sealed trait NodejsTest {
  def runTest(jsTestPath: String, loggers: Array[Logger])(implicit taskDef: TaskDef): Event
}

private class NodejsTestImpl(consoleArgs: Args,
                             jestOutputParser: JestOutputParser,
                             val testStatistics: TestStatistics)
                            (implicit config: TestFrameworkConfig) extends NodejsTest {

  @SideEffect(this.testStatistics)
  def runTest(jsTestPath: String, loggers: Array[Logger])(implicit taskDef: TaskDef): Event = {
    val startTime = Deadline.now
    loggers.info(s"Testing ${fansi.Bold.On(taskDef.fullyQualifiedName)}")
    val event = try {

      val JestFramework.NodejsCmd(cmd, args) = config.nodejsCmdOfPath(jsTestPath)
      val childProcess = ChildProcess.spawnSync(cmd, consoleArgs.combine(args)) //run code with nodejs

      val testEvent = this.resolveChildProcess(childProcess, loggers)
      testEvent
    } catch {
      case NonFatal(t) =>
        loggers.error(s"Test failed: ${fansi.Color.Red(t.getClass.getName)}:${t.getMessage}")
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
    val outputOpt = childProcess.outputOpt
    val status = childProcess.status match {
      case 0 =>
        val outputForPass = if (config.silentOnPass) childProcess.stderrOpt else outputOpt
        outputForPass.map(config.jestOutputFilter).foreach(loggers.info)
        Status.Success
      case _ =>
        outputOpt.map(config.jestOutputFilter).foreach(loggers.error)
        Status.Failure
    }
    outputOpt.foreach(this.countTestResult(_, status))
    testStatistics.nextTestSuite(status)
    JestTestEvent(status)
  }

  @SideEffect(testStatistics)
  private def countTestResult(output: String,
                              status: Status): Unit = {
    this.jestOutputParser.extractStatistics(output) match {
      case Some(testCaseResult) =>
        testStatistics.incrementPassedTest(testCaseResult.passed)
        testStatistics.incrementFailedTest(testCaseResult.failed)
      case None => if (status == Status.Failure) testStatistics.incrementFailedTest()
    }
  }
}