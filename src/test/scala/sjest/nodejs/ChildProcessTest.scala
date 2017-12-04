package sjest.nodejs

import utest._

import scala.scalajs.js

import sjest.impl.NEWLINE

object ChildProcessTest extends TestSuite {
  val tests = Tests {
    "spawnSync-basic" - {
      val childProcess = ChildProcess.spawnSync("echo", js.Array("value"))
      val output = childProcess.stdoutOpt.get
      assert(output == "value" + NEWLINE)
    }
  }

}
