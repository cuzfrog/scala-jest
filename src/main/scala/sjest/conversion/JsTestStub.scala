package sjest.conversion

import sjest.JestSuite
import sjest.support.Utils

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel

private object JsTestStub {
  @JSExportTopLevel("loadTest")
  def loadTest(fqcn: String, paths: js.Array[String]): js.Function = {
    Utils.loadModule[JestSuite](fqcn).container.queryTestCase(paths).runBlock
  }

  @JSExportTopLevel("loadControl")
  def loadControl(fqcn: String, tpe: String): js.Function = {
    val controlType = ControlType.withName(tpe)
    val controlCase = Utils.loadModule[JestSuite](fqcn).container.queryControlCase(controlType)
    controlCase.runBlock
  }
}
