package sjest.nodejs

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.|

@js.native
@JSImport("child_process", JSImport.Namespace)
private[sjest] object ChildProcess extends js.Object {
  def execSync(command: String,
               options: js.UndefOr[js.Object] = js.undefined): js.Object | String = js.native
  def spawnSync(command: String, args: Array[String],
                options: js.UndefOr[js.Object] = js.undefined): ChildProcess = js.native
}

@js.native
private[sjest] trait ChildProcess extends js.Object {
  val pid: Long = js.native
  val output: Array[Byte] | String = js.native
  val stdout: Array[Byte] | String = js.native
  val stderr: Array[Byte] | String = js.native
  val status: Int = js.native
  val signal: String = js.native
  //val error: js.Error = js.native //unreliable
}