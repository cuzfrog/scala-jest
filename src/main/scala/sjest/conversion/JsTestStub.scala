package sjest.conversion

import sjest.JestSuite

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.scalajs.reflect.Reflect

private object JsTestStub {
  @JSExportTopLevel("loadTest")
  def loadTest(fqcn: String, descr: js.UndefOr[String], testName: String): js.Function = {
    val testSuite = Reflect.lookupInstantiatableClass(fqcn) match {
      case Some(moduleClass) => moduleClass.newInstance().asInstanceOf[JestSuite]
      case None => throw new ClassNotFoundException(fqcn)
    }
    val descrOpt = descr.toOption
//    val group = testSuite.jsTestContainer.getGroups.find(_.descr == descrOpt) match {
//      case Some(g) => g
//      case None => throw new IllegalArgumentException(
//        s"Cannot find describe group '$descr' in '$fqcn', is *opt.js file stale?")
//    }
//
//    group.getTests.find(_.name == testName) match {
//      case Some(test) => test.runBlock
//      case None => throw new IllegalArgumentException(
//        s"Cannot find test '$testName' in '$fqcn', is *opt.js file stale?")
//    }
    ???
  }
}
