package sjest.conversion

import sjest.PropertyTest
import utest._

import scala.util.Random

object JsTestCaseTest extends TestSuite with PropertyTest {
  val tests = Tests {
    val mockCase = JsTestCase(Random.genAlphanumeric(10), () => ())
    "js.content" - {

    }
  }
}
