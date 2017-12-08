package sjest.nodejs

import sjest.PackageCommon
import utest._

import scala.scalajs.js

object ChildProcessTest extends sjest.BaseSuite with PackageCommon{
  val tests = Tests {
    "spawnSync-basic" - {
      val childProcess = ChildProcess.spawnSync("echo", js.Array("value"))
      val output = childProcess.stdoutOpt.get
      assert(output == "value" + NEWLINE)
    }
  }

}
