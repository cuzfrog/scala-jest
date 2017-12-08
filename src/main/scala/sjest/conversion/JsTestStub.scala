package sjest.conversion

import sjest.JestSuite

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.scalajs.reflect.Reflect

private object JsTestStub {
  @JSExportTopLevel("loadTest")
  def loadTest(fqcn: String, paths: js.Array[String]): js.Function = {
    loadSuite(fqcn).container.queryTestCase(paths).runBlock
  }

  @JSExportTopLevel("loadControl")
  def loadControl(fqcn: String, tpe: String): js.Function = {
    val controlType = ControlType.withName(tpe)
    val controlCase = loadSuite(fqcn).container.queryControlCase(controlType)
    controlCase.runBlock
  }

  private def loadSuite(fqcn: String) = {
    Reflect.lookupInstantiatableClass(fqcn) match {
      case Some(moduleClass) => moduleClass.newInstance().asInstanceOf[JestSuite]
      case None => throw new ClassNotFoundException(fqcn)
    }
  }
}
