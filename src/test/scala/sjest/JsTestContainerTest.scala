package sjest

import utest._

import scala.util.Random
import sjest.impl.ExRandom

object JsTestContainerTest extends TestSuite {

  val tests = Tests {
    val jsTestContainer = new JsTestContainer
    val name = Random.genAlphanumeric(5)

    "normal-behavior" - {
      assert(jsTestContainer.getTests.isEmpty)
      jsTestContainer.add("some description", () => "value")
      assert(jsTestContainer.getTests.size == 1)

      jsTestContainer.setName(name)
      assert(jsTestContainer.getName == name)
      assert(jsTestContainer.getFilename == name + ".test.js")

    }
    "constraint-behavior" - {
      intercept[IllegalStateException] {
        jsTestContainer.getName
      }

      intercept[IllegalStateException] {
        jsTestContainer.getFilename
      }

      intercept[IllegalArgumentException] {
        jsTestContainer.setName("bad name contains space")
      }
    }
    "order-retain-test1" - {
      jsTestContainer.add("1", () => ())
      jsTestContainer.add("3", () => ())
      jsTestContainer.add("2", () => ())
      val tests = jsTestContainer.getTests.toList
      assert(tests.head._1 == "1")
      assert(tests(1)._1 == "3")
      assert(tests(2)._1 == "2")
    }
    "order-retain-test2" - {
      jsTestContainer.add("2", () => ())
      jsTestContainer.add("3", () => ())
      jsTestContainer.add("1", () => ())
      val tests = jsTestContainer.getTests.toList
      assert(tests.head._1 == "2")
      assert(tests(1)._1 == "3")
      assert(tests(2)._1 == "1")
    }
  }
}
