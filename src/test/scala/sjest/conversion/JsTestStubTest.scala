package sjest.conversion

import sjest.MockSuccessTest
import utest._

import scala.scalajs.js

object JsTestStubTest extends TestSuite {

  var sideEffectMarker: Int = 0
  private val classname = classOf[MockSuccessTest].getName

  val tests = Tests {
    "loadTest-positive" - {
      val function = JsTestStub.loadTest(classname, Seq("mock test")).asInstanceOf[js.Function0[String]]
      assert(sideEffectMarker == 0)
      function()
      assert(sideEffectMarker == 1)
    }
    "loadTest-negative-classname" - {
      intercept[ClassNotFoundException] {
        JsTestStub.loadTest("some.bad.name", Seq("mock test"))
      }
    }
    "loadTest-negative-bad-path" - {
      intercept[IllegalArgumentException] {
        JsTestStub.loadTest(classname, Seq("bad path asdfasdf"))
      }
    }
  }
}

