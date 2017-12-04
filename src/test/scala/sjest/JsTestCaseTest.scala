package sjest

import utest._

import scala.util.Random
import sjest.impl.ExRandom

object JsTestCaseTest extends TestSuite {

  val tests = Tests {
    val jsTestCase = new JsTestCase
    val name = Random.genAlphanumeric(5)

    "normal-behavior" - {
      assert(jsTestCase.getTests.isEmpty)
      jsTestCase.add("some description", () => "value")
      assert(jsTestCase.getTests.size == 1)

      jsTestCase.setName(name)
      assert(jsTestCase.getName == name)
      assert(jsTestCase.getFilename == name + ".test.js")

    }
    "constraint-behavior" - {
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
    "order-retain-test1" - {
      jsTestCase.add("1", () => ())
      jsTestCase.add("3", () => ())
      jsTestCase.add("2", () => ())
      val tests = jsTestCase.getTests.toList
      assert(tests.head._1 == "1")
      assert(tests(1)._1 == "3")
      assert(tests(2)._1 == "2")
    }
    "order-retain-test2" - {
      jsTestCase.add("2", () => ())
      jsTestCase.add("3", () => ())
      jsTestCase.add("1", () => ())
      val tests = jsTestCase.getTests.toList
      assert(tests.head._1 == "2")
      assert(tests(1)._1 == "3")
      assert(tests(2)._1 == "1")
    }
  }
}
