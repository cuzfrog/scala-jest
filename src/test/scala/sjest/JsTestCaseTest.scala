package sjest

import utest._

import scala.util.Random

object JsTestCaseTest extends TestSuite {

  val tests = Tests {
    val jsTestCase = new JsTestCase
    val name = Random.genAlphanumeric(5)

    "normal behavior" - {
      assert(jsTestCase.getTests.isEmpty)
      jsTestCase.add("some description", () => "value")
      assert(jsTestCase.getTests.size == 1)

      jsTestCase.setName(name)
      assert(jsTestCase.getName == name)
      assert(jsTestCase.getFilename == name + ".test.js")

    }
    "constraint behavior" - {
      intercept[IllegalStateException] {
        jsTestCase.getName
      }

      intercept[IllegalStateException] {
        jsTestCase.getFilename
      }

      intercept[IllegalArgumentException] {
        jsTestCase.setName("bad name contains space")
      }
    }
  }
}
