package sjest.conversion

import sjest.impl.ExRandom
import sjest.support.MutableContext
import sjest.{JestSuite, PropertyTest}
import utest._

import scala.scalajs.js
import scala.util.Random

object JsTestContainerTest extends TestSuite with PropertyTest {

  private implicit val mutableContext: MutableContext[JestSuite] = new MutableContext[JestSuite] {}

  val tests = Tests {
    val impl = new JsTestContainer
    val suiteName = Random.genAlphanumeric(5)

    "normal-behavior" - {
      assert(impl.getTests.isEmpty)
      val testName = Random.genAlphanumeric(20)
      val returnValue = Random.genAlphanumeric(40)
      impl.add(testName, () => returnValue)
      assert(impl.getTests.size == 1)
      val test = impl.getTests.head
      assert(test.name == testName)
      val runBlock = test.runBlock.asInstanceOf[js.Function0[String]]
      assert(runBlock() == returnValue)

      impl.setSuiteName(suiteName)
      assert(impl.getSuiteName == suiteName)
      assert(impl.getFilename == suiteName + ".test.js")
    }
    "constraint-behavior" - {
      intercept[IllegalStateException] {
        impl.getSuiteName
      }
      intercept[IllegalStateException] {
        impl.getFilename
      }
      intercept[IllegalArgumentException] {
        impl.setSuiteName("bad name contains space")
      }
    }
    "negative-dupliciate-names" - {
      impl.add("same", () => ())
      intercept[IllegalArgumentException](impl.add("same", () => ()))
    }
    "order-retain" - {
      val expectedNames = (0 to Random.nextInt(50)).map { idx => Random.genAlphanumeric(2) + idx }
      expectedNames.foreach(impl.add(_, () => ()))

      val resultNames = impl.getTests.map(_.name)
      assert(resultNames == expectedNames)
    }
    "describe-group" - {
      assert(impl.getTests.isEmpty)
      val groupCnt = Random.nextInt(6) + 1
      val expectedTestNames = (0 until groupCnt).flatMap { groupId =>
        val descrOpt = if (groupId == 0) None else Some(Random.genAlphanumeric(10))
        descrOpt.foreach(impl.enterDescribe)
        val testNames = (0 to Random.nextInt(20)).map(_ => Random.genAlphanumeric(20))
        testNames.foreach(name => impl.add(name, () => ()))
        descrOpt.foreach(impl.escapeDescribe)
        testNames
      }

      val resultNames = impl.getTests.map(_.name)
      assert(resultNames == expectedTestNames)
    }
    "nested-describe-query" - {
      impl.enterDescribe("1")
      ???
      impl.enterDescribe("1")
    }
  }

  override protected def propertyTestPathFilter: Seq[String] = Seq(
    "constraint-behavior", "negative-dupliciate-names"
  )
}
