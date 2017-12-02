package sjest.nodejs

import io.scalajs.nodejs.buffer.Buffer

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.|

@js.native
@JSImport("child_process", JSImport.Namespace)
private[sjest] object ChildProcess extends js.Object {
  def execSync(command: String,
               options: js.UndefOr[js.Object] = js.undefined): Buffer | String = js.native
  def spawnSync(command: String, args: js.Array[String],
                options: js.UndefOr[js.Object] = js.undefined): ChildProcess = js.native
}

@js.native
private[sjest] trait ChildProcess extends js.Object {
  val pid: Long = js.native
  val output: Buffer | String = js.native
  val stdout: Buffer | String = js.native
  val stderr: Buffer | String = js.native
  val status: Int = js.native
  val signal: String = js.native
  val error: js.Error = js.native
}