package sjest

import sbt.testing.{Event, Logger, Status, TaskDef}
import sjest.nodejs.ChildProcess

import scala.concurrent.duration.Deadline
import scala.util.control.NonFatal

private object NodejsTestImpl {

  def runTest(jsTestPath: String, loggers: Array[Logger])
             (implicit taskDef: TaskDef, config: TestFrameworkConfig): Event = {
    val startTime = Deadline.now
    loggers.foreach(_.info(s"---jest--- Testing against: ${taskDef.fullyQualifiedName}"))
    val event = try {

      val JestFramework.NodejsCmd(cmd, args) = config.nodejsCmdOfPath(jsTestPath)
      val childProcess = ChildProcess.spawnSync(cmd, args) //run code with nodejs

      childProcess.status match {
        case 0 =>
          val output = Option(childProcess.output)
          output.foreach(out => loggers.foreach(_.info(out.toString)))
          loggers.foreach(_.info(s"PASS ${taskDef.fullyQualifiedName}"))
          JestTestEvent(Status.Success)
        case _ =>
          loggers.foreach(_.error(s"test failed: "))
          JestTestEvent(Status.Failure)
      }
    } catch {
      case NonFatal(t) =>
        loggers.foreach(_.error(s"test failed with $t}"))
        JestTestEvent(Status.Failure)
    }

    val duration = (Deadline.now - startTime).toMillis
    event.copy(duration = duration)
  }



}
