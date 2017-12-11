package sjest.impl

import sjest.PropertyTest
import utest._

import scala.util.Random

object TestCaseResultTest extends sjest.BaseSuite with PropertyTest {

  override protected def propertyTestPathExcluded: Seq[String] = Seq("fromJsonNegative")

  val tests = Tests {
    'serialization - {
      val expected = TestCaseResult(Random.nextInt(3), Random.nextInt(10))
      val deserialized = TestCaseResult.fromJson(expected.toJson)
      assert(expected == deserialized)
    }
    'fromJsonNegative - {
      val badJson = "{'failed':0,'bad':1}"
      intercept[IllegalArgumentException] {
        TestCaseResult.fromJson(badJson)
      }
    }
  }
}
