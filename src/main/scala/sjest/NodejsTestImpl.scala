package sjest

import io.scalajs.nodejs.fs.Fs
import sbt.testing.{Event, Logger, Status, TaskDef}
import sjest.nodejs.{ChildProcess, ChildProcessOpt}
import sjest.support.VisiableForTest

import scala.concurrent.duration.Deadline
import scala.util.control.NonFatal

private object NodejsTestImpl {

  def runTest(jsTestPath: String, loggers: Array[Logger])
             (implicit taskDef: TaskDef, config: TestFrameworkConfig): Event = {
    val startTime = Deadline.now
    loggers.info(s"---jest--- Testing against: ${taskDef.fullyQualifiedName}")
    val event = try {

      val JestFramework.NodejsCmd(cmd, args) = config.nodejsCmdOfPath(jsTestPath)
      if (!Fs.existsSync(cmd)) throw new IllegalArgumentException(s"Cannot find $cmd. Have you installed it?")
      val childProcess = ChildProcess.spawnSync(cmd, args) //run code with nodejs

      this.resolveChildProcess(childProcess, loggers)
    } catch {
      case NonFatal(t) =>
        loggers.error(s"Test failed with $t}")
        JestTestEvent(Status.Failure)
    }

    val duration = (Deadline.now - startTime).toMillis
    event.copy(duration = duration)
  }

  private def resolveChildProcess(childProcess: ChildProcessOpt, loggers: Array[Logger])
                                 (implicit taskDef: TaskDef): JestTestEvent = {
    childProcess.status match {
      case 0 =>
        val output = childProcess.outputOpt.map(parseJestOutput)
        output.foreach(out => loggers.info(out))
        JestTestEvent(Status.Success)
      case _ =>
        childProcess.stderrOpt.foreach(loggers.error)

//        loggers.error(s"test failed with error: ${childProcess.error}")
//        loggers.foreach(_.error(s"${childProcess.stderr.toString}"))

        JestTestEvent(Status.Failure)
    }
  }

  @VisiableForTest
  private[sjest] def parseJestOutput(in: String): String = {
    in
  }
}
