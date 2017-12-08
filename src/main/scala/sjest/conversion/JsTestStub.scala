package sjest.conversion

import sjest.JestSuite

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.scalajs.reflect.Reflect

private object JsTestStub {
  @JSExportTopLevel("loadTest")
  def loadTest(fqcn: String, paths: js.Array[String]): js.Function = {
    val testSuite = Reflect.lookupInstantiatableClass(fqcn) match {
      case Some(moduleClass) => moduleClass.newInstance().asInstanceOf[JestSuite]
      case None => throw new ClassNotFoundException(fqcn)
    }
    testSuite.jsTestContainer.queryTestCase(paths).runBlock
  }
}
