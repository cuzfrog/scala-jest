package sjest.impl

import sjest.JestSuite

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.scalajs.reflect.Reflect

private object JsTestStub {
  @JSExportTopLevel("loadTest")
  def loadTest(fqcn: String, keyDescription: String): js.Function = {
    val testSuite = Reflect.lookupInstantiatableClass(fqcn) match {
      case Some(moduleClass) => moduleClass.newInstance().asInstanceOf[JestSuite]
      case None => throw new ClassNotFoundException(fqcn)
    }

    testSuite.jsTestContainer.getTests
      .collectFirst { case (desc, cb) if desc == keyDescription => cb } match {
      case Some(codeBlock) => codeBlock
      case None => throw new IllegalArgumentException(
        s"Cannot find test '$keyDescription' in '$fqcn', is *opt.js file stale?")
    }
  }
}
