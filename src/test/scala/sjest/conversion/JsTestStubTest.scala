package sjest.conversion

import sjest.MockSuccessTest
import utest._

import scala.scalajs.js

object JsTestStubTest extends TestSuite {

  var sideEffectMarker: Int = 0
  private val classname = classOf[MockSuccessTest].getName

  val tests = Tests {
    "loadTest-positive" - {
      val function = JsTestStub.loadTest(classname, js.undefined, "mock test").asInstanceOf[js.Function0[String]]
      assert(sideEffectMarker == 0)
      function()
      assert(sideEffectMarker == 1)
    }
    "loadTest-negative-classname" - {
      intercept[ClassNotFoundException] {
        JsTestStub.loadTest("some.bad.name", js.undefined, "mock test")
      }
    }
    "loadTest-negative-bad-id" - {
      intercept[IllegalArgumentException] {
        JsTestStub.loadTest(classname, "bad descr", "mock test")
      }
      intercept[IllegalArgumentException] {
        JsTestStub.loadTest(classname, js.undefined, "bad test name")
      }
    }
  }
}

