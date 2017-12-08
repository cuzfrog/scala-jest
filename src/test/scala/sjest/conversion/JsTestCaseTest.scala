package sjest.conversion

import sjest.PropertyTest
import utest._

import scala.util.Random

object JsTestCaseTest extends sjest.BaseSuite with PropertyTest {
  val tests = Tests {
    val mockCase = JsTestCase(Random.genAlphanumeric(10), () => ())
    "js.content" - {
      val fqcn = Random.genAlphanumeric(8)
      val content = mockCase.toJsTest(Seq(fqcn, mockCase.name))
      val expectedContent =
        s"""test('${mockCase.name}', () => {
           |  loadTest('$fqcn',['${mockCase.name}'])();
           |});""".stripMargin
    }
  }
}
