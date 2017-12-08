package sjest.conversion

import sjest.PropertyTest
import utest._

import scala.util.Random

object JsTestCaseTest extends TestSuite with PropertyTest {
  val tests = Tests {
    val mockCase = JsTestCase(Random.genAlphanumeric(10), () => ())
    "js.content" - {
      val upperPath = Random.genAlphanumeric(8)
      val content = mockCase.toJsTest(Seq(upperPath, mockCase.name))
      val expectedContent =
        s"""test('${mockCase.name}', () => {
           |  loadTest(['$upperPath','${mockCase.name}'])();
           |});""".stripMargin
    }
  }
}
