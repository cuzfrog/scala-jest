package sjest.conversion

import sjest.JestSuite
import utest._

import scala.util.Random

object JsTestGroupTest extends sjest.BaseSuite {

  import JestSuite.mutableContext

  val tests = Tests {
    val mockTopGroup = new JsTestGroup()
    val suiteName = Random.genAlphanumeric(15) + "_suite"
    "behavior" - {
      val mockTests = (1 to Random.nextInt(30)).map { idx =>
        JsTestCase
      }
      //mockTopGroup.addTest()
    }
    val testName = Random.genAlphanumeric(15) + "_test"
    "toJsTest-content" - {
      mockTopGroup.addTest(testName, () => ())
      val content = mockTopGroup.toJsTest(Seq(suiteName))
      val expectedContent =
        s"""test('$testName', () => {
           |  loadTest(['$suiteName','$testName'])();
           |});""".stripMargin
      assert(content == expectedContent)
    }
    val description = Random.genAlphanumeric(15) + "_description"
    "toJsTest-content-nested" - {
      val childGroup = mockTopGroup.addDescribe(description)
      childGroup.addTest(testName, () => ())
      val content = mockTopGroup.toJsTest(Seq(suiteName))
      val expectedContent =
        s"""describe('$description', () => {
           |test('$testName', () => {
           |  loadTest(['$suiteName','$description','$testName'])();
           |});
           |});""".stripMargin
      assert(content == expectedContent)
    }
  }
}
