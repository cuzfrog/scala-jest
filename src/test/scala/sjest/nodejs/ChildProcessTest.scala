package sjest.nodejs

import utest._

import scala.scalajs.js

import sjest.NEWLINE

object ChildProcessTest extends TestSuite {
  val tests = Tests {
    "spawnSync-basic" - {
      val childProcess = ChildProcess.spawnSync("echo", js.Array("value"))
      val output = childProcess.stdout.toString
      assert(output == "value" + NEWLINE)
    }
  }

}
