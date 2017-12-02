package sjest.nodejs

import io.scalajs.nodejs.buffer.Buffer

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.|

@js.native
@JSImport("child_process", JSImport.Namespace)
private object ChildProcessJS extends js.Object {
  def execSync(command: String,
               options: js.UndefOr[js.Object] = js.undefined): Buffer | String = js.native
  def spawnSync(command: String, args: js.Array[String],
                options: js.UndefOr[js.Object] = js.undefined): ChildProcessJS = js.native
}

@js.native
private trait ChildProcessJS extends js.Object {
  val pid: Int = js.native
  val output: Buffer | String = js.native
  val stdout: Buffer | String = js.native
  val stderr: Buffer | String = js.native
  val status: Int = js.native
  val signal: String = js.native
  val error: js.Error = js.native
}

private[sjest] object ChildProcess {
  def execSync(command: String,
               options: js.UndefOr[js.Object] = js.undefined): String = {
    ChildProcessJS.execSync(command, options).toString
  }

  def spawnSync(command: String, args: js.Array[String],
                options: js.UndefOr[js.Object] = js.undefined): ChildProcessOpt = {
    val childProcessJS = ChildProcessJS.spawnSync(command, args, options)
    new ChildProcessOpt(childProcessJS)
  }
}

private[sjest] final class ChildProcessOpt private[nodejs](childProcess: ChildProcessJS) {
  val pid: Int = childProcess.pid
  val outputOpt: Option[String] = Option(childProcess.output.toString)
  val stdoutOpt: Option[String] = Option(childProcess.stdout.toString)
  val stderrOpt: Option[String] = Option(childProcess.stderr.toString)
  val status: Int = childProcess.status
  val signal: String = childProcess.signal
  val errorOpt: Option[js.Error] = Option(childProcess.error)
}