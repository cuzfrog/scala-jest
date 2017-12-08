package sjest.conversion

import sjest.{BaseSuite, PropertyTest}
import utest._

import scala.util.Random

object JsControlCaseTest extends BaseSuite with PropertyTest {
  val tests = Tests {
    val suiteName = Random.genAlphanumeric(10) + "_suite"
    "js-content" - {
      val mockTpe = ControlType.values(Random.nextInt(ControlType.values.length))
      val mockCase = JsControlCase(mockTpe, () => ())
      val content = mockCase.toJsTest(suiteName)
      val methodName = decapitalize(mockTpe.toString)

      val expectedContent =
        s"""$methodName(() => {
           |  loadControl('$suiteName','$mockTpe')();
           |});""".stripMargin
      assert(content == expectedContent)
    }
    "ControlType.decapitalize" - {
      val decapitalized = ControlType.decapitalize(suiteName)
      val expected = this.decapitalize(suiteName)
      assert(decapitalized == expected)
    }
  }

  private def decapitalize(in: String): String = {
    if (in.isEmpty) in
    else {
      val first :: tail = in.toCharArray.toList
      (first.toLower :: tail).mkString
    }
  }
}
