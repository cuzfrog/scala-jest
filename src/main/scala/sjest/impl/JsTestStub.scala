package sjest.impl

import sjest.JestSuite

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.scalajs.reflect.Reflect

private object JsTestStub {
  @JSExportTopLevel("loadTest")
  def loadTest(fqcn: String, testName: String): js.Function = {
    val testSuite = Reflect.lookupInstantiatableClass(fqcn) match {
      case Some(moduleClass) => moduleClass.newInstance().asInstanceOf[JestSuite]
      case None => throw new ClassNotFoundException(fqcn)
    }

    testSuite.jsTestContainer.getTests.find(_.name == testName) match {
      case Some(test) => test.runBlock
      case None => throw new IllegalArgumentException(
        s"Cannot find test '$testName' in '$fqcn', is *opt.js file stale?")
    }
  }
}
