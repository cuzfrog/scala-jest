package sjest.conversion

import sjest.MockSuccessTest
import utest._

import scala.scalajs.js

object JsTestStubTest extends sjest.BaseSuite {

  var sideEffectMarker: Int = 0
  private val classname = MockSuccessTest.getClass.getName

  val tests = Tests {
    "loadTest-positive" - {
      val function = JsTestStub.loadTest(classname, js.Array("mock test")).asInstanceOf[js.Function0[String]]
      assert(sideEffectMarker == 0)
      function()
      assert(sideEffectMarker == 1)
    }
    "loadTest-negative-classname" - {
      intercept[ClassNotFoundException] {
        JsTestStub.loadTest("some.bad.name", js.Array("mock test"))
      }
    }
    "loadTest-negative-bad-path" - {
      intercept[IllegalArgumentException] {
        JsTestStub.loadTest(classname, js.Array("bad path asdfasdf"))
      }
    }
  }
}

